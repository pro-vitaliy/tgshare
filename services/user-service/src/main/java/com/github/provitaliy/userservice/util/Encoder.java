package com.github.provitaliy.userservice.util;

import com.github.provitaliy.userservice.exception.InvalidEncryptedIdException;
import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Encoder {
    private final Hashids hashids;

    public String encode(Long id) {
        return hashids.encode(id);
    }

    public Long decode(String hash) {
        long[] result = hashids.decode(hash);

        if (result.length == 0) {
            throw new InvalidEncryptedIdException("Invalid id: " + hash);
        }
        return result[0];
    }
}
