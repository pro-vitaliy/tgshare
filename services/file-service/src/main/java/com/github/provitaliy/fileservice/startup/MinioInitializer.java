package com.github.provitaliy.fileservice.startup;

import com.github.provitaliy.fileservice.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinioInitializer implements ApplicationRunner {
    private final MinioClient minioClient;
    private final MinioProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        String bucketName = properties.getBucket();
        try {
            log.info("Checking MinIO bucket '{}' existence...", bucketName);
            boolean isExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build()
            );
            if (!isExists) {
                throw new IllegalStateException("Required MinIO bucket '" + bucketName + "' does not exist");
            }
            log.info("MinIO bucket '{}' exists and is accessible", bucketName);
        } catch (Exception e) {
            log.error("Failed to connect to MinIO or verify bucket '{}': {}", bucketName, e.getMessage(), e);
            throw new IllegalStateException("MinIO storage is unavailable", e);
        }
    }
}
