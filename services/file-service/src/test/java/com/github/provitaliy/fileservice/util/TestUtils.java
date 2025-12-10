package com.github.provitaliy.fileservice.util;

import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.fileservice.config.FileServiceProperties;
import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.entity.FileMetadata;
import lombok.experimental.UtilityClass;
import net.datafaker.Faker;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TestUtils {
    private static final Faker faker = new Faker();
    public static FileUploadEvent generateFileUploadEvent() {
        return new FileUploadEvent(
                String.valueOf(faker.number().randomNumber()),
                faker.number().randomNumber(),
                faker.file().fileName(),
                faker.file().mimeType(),
                faker.number().randomNumber()
        );
    }

    public static FileServiceProperties generateFileServiceProperties() {
        FileServiceProperties props = new FileServiceProperties();
        props.setUrl(faker.internet().url());
        props.setHost(faker.internet().domainName());
        props.setPort(faker.number().numberBetween(8000, 9000));
        props.setDownloadEndpoint("/download/");
        return props;
    }

    public static MinioProperties generateMinioProperties() {
        MinioProperties props = new MinioProperties();
        props.setBucket(faker.lorem().characters(3, 10).toLowerCase());
        props.setDefaultTtlDays(faker.number().numberBetween(1, 30));
        return props;
    }

    public static FileMetadata generateFileMetadata() {
        return FileMetadata.builder()
                .id(faker.number().randomNumber())
                .uuid(faker.internet().uuid())
                .ownerId(faker.number().randomNumber())
                .fileName(faker.file().fileName())
                .mimeType(faker.file().mimeType())
                .fileSize(faker.number().randomNumber())
                .bucket(faker.lorem().characters(3, 10).toLowerCase())
                .objectName(faker.lorem().word())
                .uploadedAt(faker.timeAndDate().past(10, TimeUnit.DAYS))
                .expiresAt(faker.timeAndDate().future(10, TimeUnit.DAYS))
                .build();
    }
}
