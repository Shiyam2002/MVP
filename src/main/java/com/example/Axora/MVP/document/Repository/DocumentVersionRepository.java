package com.example.Axora.MVP.document.Repository;

import com.example.Axora.MVP.document.Entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, UUID> {

    List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(UUID documentId);

    Optional<DocumentVersion> findTopByDocumentIdOrderByVersionNumberDesc(UUID documentId);
}
