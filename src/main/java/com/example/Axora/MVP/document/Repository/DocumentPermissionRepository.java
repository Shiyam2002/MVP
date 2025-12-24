package com.example.Axora.MVP.document.Repository;

import com.example.Axora.MVP.document.Entity.DocumentPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentPermissionRepository
        extends JpaRepository<DocumentPermission, UUID> {

    Optional<DocumentPermission> findByDocumentIdAndUserId(UUID documentId, UUID userId);

    List<DocumentPermission> findByUserId(UUID userId);
}
