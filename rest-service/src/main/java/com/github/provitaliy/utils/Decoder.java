package com.github.provitaliy.utils;

import com.github.provitaliy.exception.InvalidActivationLinkException;
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
            throw new InvalidActivationLinkException("Invalid activation id: " + hash);
        }
        return result[0];
    }
}
