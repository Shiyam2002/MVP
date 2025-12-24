package com.example.Axora.MVP.document.Repository;

import com.example.Axora.MVP.document.Entity.DocumentExtractionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentExtractionJobRepository
        extends JpaRepository<DocumentExtractionJob, UUID> {

    List<DocumentExtractionJob> findByDocumentId(UUID documentId);

    List<DocumentExtractionJob> findByStatus(String status);
}

