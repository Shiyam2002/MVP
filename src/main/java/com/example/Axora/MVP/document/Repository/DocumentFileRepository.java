package com.example.Axora.MVP.document.Repository;

import com.example.Axora.MVP.document.Entity.DocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentFileRepository
        extends JpaRepository<DocumentFile, UUID> {

    List<DocumentFile> findByDocumentId(UUID documentId);

    List<DocumentFile> findByVersionId(UUID versionId);
}

