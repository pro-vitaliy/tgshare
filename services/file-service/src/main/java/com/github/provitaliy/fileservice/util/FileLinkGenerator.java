package com.github.provitaliy.fileservice.util;

import com.github.provitaliy.fileservice.config.FileServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileLinkGenerator {
    private final FileServiceProperties props;

    public String generateLink(String uuid) {
        return String.format(
                "http://%s:%d%s%s",
                props.getHost(),
                props.getPort(),
                props.getDownloadEndpoint(),
                uuid
        );
    }
}
