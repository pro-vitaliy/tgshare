package com.github.provitaliy.service;

import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;

public interface FileService {
    AppDocument getDocument(String codedId);
    AppPhoto getPhoto(String codedId);
}
