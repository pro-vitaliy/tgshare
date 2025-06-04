package com.github.provitaliy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binary_content")
@Getter
@Setter
@NoArgsConstructor
public class BinaryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte[] fileAsArrayOfBytes;

    public BinaryContent(byte[] fileAsArrayOfBytes) {
        this.fileAsArrayOfBytes = fileAsArrayOfBytes;
    }
}
