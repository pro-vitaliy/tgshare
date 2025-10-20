package com.github.provitaliy.service;

import com.github.provitaliy.repository.AppDocumentRepository;
import com.github.provitaliy.repository.AppPhotoRepository;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.exception.ResourceNotFoundException;
import com.github.provitaliy.utils.Decoder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Getter
@Setter
@RequiredArgsConstructor
public class FileService {
    private final AppDocumentRepository appDocumentRepository;
    private final AppPhotoRepository appPhotoRepository;
    private final Decoder decoder;

    public AppDocument getDocument(String codedId) {
        var id = decoder.decodeId(codedId);
        return appDocumentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Документ с id {} не найден!", id);
                    return new ResourceNotFoundException("Документ не найден!");
                });
    }

    public AppPhoto getPhoto(String codedId) {
        var id = decoder.decodeId(codedId);
        return appPhotoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Фото с id {} не найдено!", id);
                    return new ResourceNotFoundException("Фото не найдено!");
                });
    }
}

