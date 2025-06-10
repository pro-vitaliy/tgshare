package com.github.provitaliy.controller;

import com.github.provitaliy.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDocument(@RequestParam("id") String codedId, HttpServletResponse response) {
        var document = fileService.getDocument(codedId);

        response.setContentType(MediaType.parseMediaType(document.getMimeType()).toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getDocName() + "\"");
        response.setStatus(HttpServletResponse.SC_OK);

        try (var output = response.getOutputStream()) {
            output.write(document.getBinaryContent().getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при скачивании файла", e
            );
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String codedId, HttpServletResponse response) {
        var photo = fileService.getPhoto(codedId);

        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-Disposition", "attachment;");
        response.setStatus(HttpServletResponse.SC_OK);

        try (var output = response.getOutputStream()) {
            output.write(photo.getBinaryContent().getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при скачивании файла", e
            );
        }
    }
}
