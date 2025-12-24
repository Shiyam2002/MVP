package com.example.Axora.MVP.document.Route.V1;

import com.example.Axora.MVP.document.DTO.Request.CreateDocumentRequest;
import com.example.Axora.MVP.document.DTO.Request.UploadDocumentFileRequest;
import com.example.Axora.MVP.document.Entity.Document;
import com.example.Axora.MVP.document.Entity.DocumentVersion;
import com.example.Axora.MVP.document.Service.DocumentService;
import com.example.Axora.MVP.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final SecurityUtils authUtil;

    /* ================= CREATE DOCUMENT ================= */

    @PostMapping
    public ResponseEntity<Document> createDocument(
            @RequestBody @Valid CreateDocumentRequest request
    ) {
        UUID userId = authUtil.getCurrentUser();
        Document document = documentService.createDocument(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    /* ================= UPLOAD FILE / CREATE VERSION ================= */

    @PostMapping("/{documentId}/upload")
    public ResponseEntity<DocumentVersion> uploadFile(
            @PathVariable UUID documentId,
            @RequestBody @Valid UploadDocumentFileRequest request
    ) {
        UUID userId = authUtil.getCurrentUser();
        request.setDocumentId(documentId);

        DocumentVersion version =
                documentService.uploadFile(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }

    /* ================= GET VERSIONS ================= */

    @GetMapping("/{documentId}/versions")
    public ResponseEntity<List<DocumentVersion>> getVersions(
            @PathVariable UUID documentId
    ) {
        UUID userId = authUtil.getCurrentUser();
        return ResponseEntity.ok(
                documentService.getVersions(documentId, userId)
        );
    }

    /* ================= ARCHIVE DOCUMENT ================= */

    @PatchMapping("/{documentId}/archive")
    public ResponseEntity<Void> archiveDocument(
            @PathVariable UUID documentId
    ) {
        UUID userId = authUtil.getCurrentUser();
        documentService.archiveDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    /* ================= DELETE DOCUMENT ================= */

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable UUID documentId
    ) {
        UUID userId = authUtil.getCurrentUser();
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }
}

