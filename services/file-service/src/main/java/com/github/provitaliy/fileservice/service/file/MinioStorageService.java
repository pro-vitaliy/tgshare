package com.github.provitaliy.fileservice.service.file;

import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.exception.MinioStorageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MinioStorageService {
    private final MinioClient minioClient;
    private final MinioProperties props;

    public String uploadFile(byte[] fileBytes, String originalName, String contentType) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes)) {
            String fileName = UUID.randomUUID() + "_" + originalName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.getBucket())
                            .object(fileName)
                            .stream(bais, fileBytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("Файл '{}' успешно загружен в bucket '{}'", fileName, props.getBucket());
            return fileName;
        } catch (Exception e) {
            log.error("Ошибка при загрузке файла в MinIO", e);
            throw new MinioStorageException("Не удалось сохранить файл в MinIO", e);
        }
    }

    public byte[] downloadFile(String fileName) {
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(props.getBucket())
                        .object(fileName)
                        .build()
        )) {
            return is.readAllBytes();
        } catch (Exception e) {
            log.error("Ошибка при скачивании файла '{}' из MinIO", fileName, e);
            throw new MinioStorageException("Не удалось скачать файл из MinIO", e);
        }
    }

    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(props.getBucket())
                            .object(fileName)
                            .build()
            );
            log.info("Файл '{}' удалён из MinIO", fileName);
        } catch (Exception e) {
            log.error("Ошибка при удалении файла '{}' из MinIO", fileName, e);
            throw new MinioStorageException("Не удалось удалить файл из MinIO", e);
        }
    }
}
