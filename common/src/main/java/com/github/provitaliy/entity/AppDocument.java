package com.github.provitaliy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_document")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramField;
    private String docName;

    @OneToOne
    private BinaryContent binaryContent;

    private String mimeType;
    private Long fileSize;
}
