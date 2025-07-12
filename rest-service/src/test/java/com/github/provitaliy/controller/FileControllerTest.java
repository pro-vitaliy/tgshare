package com.github.provitaliy.controller;

import com.github.provitaliy.dao.AppDocumentDAO;
import com.github.provitaliy.dao.AppPhotoDAO;
import com.github.provitaliy.dao.BinaryContentDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.entity.BinaryContent;
import com.github.provitaliy.service.FileService;
import org.hashids.Hashids;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileService fileService;

    @Autowired
    private AppDocumentDAO appDocumentDAO;

    @Autowired
    private AppPhotoDAO appPhotoDAO;

    @Autowired
    private BinaryContentDAO binaryContentDAO;

    @Autowired
    private Hashids hashids;

    private AppDocument document;
    private AppPhoto photo;

    @BeforeEach
    void beforeEach() {
        appDocumentDAO.deleteAll();
        appPhotoDAO.deleteAll();
        binaryContentDAO.deleteAll();

        byte[] docBytes = "test document".getBytes();
        byte[] photoBytes = "test photo".getBytes();
        BinaryContent binaryDocument = binaryContentDAO.save(new BinaryContent(docBytes));
        BinaryContent binaryPhoto = binaryContentDAO.save(new BinaryContent(photoBytes));

        document = AppDocument.builder()
                .docName("document.txt")
                .mimeType("text/plain")
                .binaryContent(binaryDocument)
                .build();

        photo = AppPhoto.builder()
                .binaryContent(binaryPhoto)
                .build();

        document = appDocumentDAO.save(document);
        photo = appPhotoDAO.save(photo);
    }

    @Test
    void shouldReturnDocument() throws Exception {
        String encodedDocId = hashids.encode(document.getId());

        mockMvc.perform(get("/api/file/get-doc")
                        .param("id", encodedDocId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" +
                        document.getDocName() + "\""))
                .andExpect(content().bytes(document.getBinaryContent().getFileAsArrayOfBytes()));
    }

    @Test
    void shouldReturnPhoto() throws Exception {
        String encodedPhotoId = hashids.encode(photo.getId());

        mockMvc.perform(get("/api/file/get-photo")
                        .param("id", encodedPhotoId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG.toString()))
                .andExpect(header().string("Content-Disposition", "attachment;"))
                .andExpect(content().bytes(photo.getBinaryContent().getFileAsArrayOfBytes()));
    }

    @Test
    void shouldReturn404() throws Exception {
        String encodedFakeId = hashids.encode(999L);

        mockMvc.perform(get("/api/file/get-doc")
                        .param("id", encodedFakeId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Документ не найден!"));
    }
}
