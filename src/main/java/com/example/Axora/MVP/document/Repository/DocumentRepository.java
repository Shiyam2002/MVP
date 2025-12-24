package com.example.Axora.MVP.document.Repository;

import com.example.Axora.MVP.document.Entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository
        extends JpaRepository<Document, UUID>,
        JpaSpecificationExecutor<Document> {

    List<Document> findByWorkspaceId(UUID workspaceId);

    boolean existsByWorkspaceIdAndTitle(UUID workspaceId, String title);
}
