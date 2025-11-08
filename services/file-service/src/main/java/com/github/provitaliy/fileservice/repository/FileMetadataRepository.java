package com.github.provitaliy.fileservice.repository;

import com.github.provitaliy.fileservice.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata , Long> {
    Optional<FileMetadata> findByUuid(String uuid);
}
