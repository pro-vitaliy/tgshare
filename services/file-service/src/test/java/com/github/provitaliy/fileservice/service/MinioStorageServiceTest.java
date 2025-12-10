package com.github.provitaliy.fileservice.service;

import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.exception.MinioStorageException;
import com.github.provitaliy.fileservice.service.file.MinioStorageService;
import com.github.provitaliy.fileservice.util.TestUtils;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MinioStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioStorageService service;

    @BeforeEach
    void setUp() {
        MinioProperties properties = TestUtils.generateMinioProperties(); // bucket, url, etc
        service = new MinioStorageService(minioClient, properties);
    }

    @Test
    void shouldUploadFileSuccessfully() throws Exception {
        // given
        var is = new ByteArrayInputStream("test".getBytes());
        long size = 4L;
        String origName = "file.txt";
        String mimeType = "text/plain";

        ObjectWriteResponse response = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenReturn(response);

        // when
        String resultFileName = service.uploadFile(is, size, origName, mimeType);

        // then
        assertNotNull(resultFileName);
        assertTrue(resultFileName.contains(origName));

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldThrowMinioStorageExceptionOnUploadFailure() throws Exception {
        // given
        var is = new ByteArrayInputStream("test".getBytes());
        doThrow(new RuntimeException("Minio error"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        // when / then
        assertThrows(
                MinioStorageException.class,
                () -> service.uploadFile(is, 4L, "file.txt", "text/plain")
        );
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldDownloadFileSuccessfully() throws Exception {
        // given
        String objectName = "objectName";
        GetObjectResponse getObjectResponse = mock(GetObjectResponse.class);
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(getObjectResponse);

        InputStream result = service.getFileStream(objectName);
        assertNotNull(result);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void shouldThrowMinioStorageExceptionOnDownloadFailure() throws Exception {
        // given
        String objectName = "abc.txt";
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("Minio error"));

        // when / then
        assertThrows(
                MinioStorageException.class,
                () -> service.getFileStream(objectName)
        );
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void shouldDeleteFileSuccessfully() throws Exception {
        // given
        String fileName = "abc.txt";
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // when
        service.deleteFile(fileName);

        // then
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void shouldThrowMinioStorageExceptionOnDeleteFailure() throws Exception {
        // given
        String fileName = "abc.txt";
        doThrow(new RuntimeException("Minio error"))
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // when / then
        assertThrows(
                MinioStorageException.class,
                () -> service.deleteFile(fileName)
        );
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
}
