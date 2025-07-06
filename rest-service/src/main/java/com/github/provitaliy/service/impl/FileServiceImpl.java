package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.AppDocumentDAO;
import com.github.provitaliy.dao.AppPhotoDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.exception.ResourceNotFoundException;
import com.github.provitaliy.service.FileService;
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
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final Decoder decoder;

    @Override
    public AppDocument getDocument(String codedId) {
        var id = decoder.decodeId(codedId);
        return appDocumentDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Документ с id {} не найден!", id);
                    return new ResourceNotFoundException("Документ не найден!");
                });
    }

    @Override
    public AppPhoto getPhoto(String codedId) {
        var id = decoder.decodeId(codedId);
        return appPhotoDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Фото с id {} не найдено!", id);
                    return new ResourceNotFoundException("Фото не найдено!");
                });
    }
}
