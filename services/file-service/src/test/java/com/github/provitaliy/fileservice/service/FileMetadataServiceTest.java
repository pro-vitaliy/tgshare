package com.github.provitaliy.fileservice.service;

import com.github.provitaliy.fileservice.entity.FileMetadata;
import com.github.provitaliy.fileservice.exception.FileMetadataNotFoundException;
import com.github.provitaliy.fileservice.exception.MetadataSaveException;
import com.github.provitaliy.fileservice.repository.FileMetadataRepository;
import com.github.provitaliy.fileservice.service.file.FileMetadataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileMetadataServiceTest {

    @Mock
    private FileMetadataRepository repository;

    @InjectMocks
    private FileMetadataService service;

    @Test
    void shouldFindMetadataByUuid() {
        // given
        String uuid = "uuid123";
        FileMetadata meta = FileMetadata.builder()
                .uuid(uuid)
                .build();

        when(repository.findByUuid(uuid)).thenReturn(Optional.of(meta));

        // when
        FileMetadata result = service.findMetadataByUuid(uuid);

        // then
        assertEquals(uuid, result.getUuid());
        verify(repository).findByUuid(uuid);
    }

    @Test
    void shouldThrowWhenMetadataNotFound() {
        // given
        String uuid = "missing";
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when / then
        assertThrows(FileMetadataNotFoundException.class,
                () -> service.findMetadataByUuid(uuid));

        verify(repository).findByUuid(uuid);
    }

    @Test
    void shouldSaveMetadataSuccessfully() {
        // given
        Long ownerId = 100L;
        String fileName = "image.png";
        String mimeType = "image/png";
        Long size = 123L;
        String bucket = "test-bucket";
        String objectName = "obj-1";
        Duration ttl = Duration.ofHours(1);

        ArgumentCaptor<FileMetadata> captor = ArgumentCaptor.forClass(FileMetadata.class);
        when(repository.save(Mockito.<FileMetadata>any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        FileMetadata result = service.saveMetadata(
                ownerId,
                fileName,
                mimeType,
                size,
                bucket,
                objectName,
                ttl
        );

        // then
        verify(repository).save(captor.capture());
        FileMetadata saved = captor.getValue();

        assertEquals(ownerId, saved.getOwnerId());
        assertEquals(fileName, saved.getFileName());
        assertEquals(mimeType, saved.getMimeType());
        assertEquals(size, saved.getFileSize());
        assertEquals(bucket, saved.getBucket());
        assertEquals(objectName, saved.getObjectName());

        assertNotNull(saved.getUuid());
        assertNotNull(saved.getUploadedAt());
        assertNotNull(saved.getExpiresAt());

        assertEquals(saved.getUploadedAt().plus(ttl), saved.getExpiresAt());
    }

    @Test
    void shouldThrowMetadataSaveExceptionOnFailure() {
        // given
        when(repository.save(Mockito.<FileMetadata>any())).thenThrow(new RuntimeException("DB error"));

        // when / then
        assertThrows(MetadataSaveException.class, () ->
                service.saveMetadata(
                        1L,
                        "file.png",
                        "image/png",
                        10L,
                        "bucket",
                        "objectName",
                        Duration.ofMinutes(5)
                )
        );
        verify(repository).save(Mockito.<FileMetadata>any());
    }

    @Test
    void shouldDeleteMetadata() {
        // given
        String objectName = "obj123";

        // when
        service.deleteMetadata(objectName);

        // then
        verify(repository).deleteByObjectName(objectName);
    }
}
