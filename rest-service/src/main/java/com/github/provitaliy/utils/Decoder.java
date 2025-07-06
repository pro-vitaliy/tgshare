package com.github.provitaliy.utils;

import com.github.provitaliy.exception.InvalidEncryptedIdException;
import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Decoder {
    private final Hashids decoder;

    public Long decodeId(String hash) {
        long[] result = decoder.decode(hash);
        if (result.length == 0) {
            throw new InvalidEncryptedIdException("Invalid id: " + hash);
        }
        return result[0];
    }
}
