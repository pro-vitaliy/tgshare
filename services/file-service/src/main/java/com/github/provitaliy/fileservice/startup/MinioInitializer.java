package com.github.provitaliy.fileservice.startup;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinioInitializer implements ApplicationRunner {
    @Value("${minio.bucket}")
    private String bucket;
    private final MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("Checking MinIO bucket '{}' existence...", bucket);
            boolean isExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );
            if (!isExists) {
                throw new IllegalStateException("Required MinIO bucket '" + bucket + "' does not exist");
            }
            log.info("MinIO bucket '{}' exists and is accessible", bucket);
        } catch (Exception e) {
            log.error("Failed to connect to MinIO or verify bucket '{}': {}", bucket, e.getMessage(), e);
            throw new IllegalStateException("MinIO storage is unavailable", e);
        }
    }
}
