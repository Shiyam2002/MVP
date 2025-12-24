package com.example.Axora.MVP.document.Service;

import com.example.Axora.MVP.document.DTO.Request.CreateDocumentRequest;
import com.example.Axora.MVP.document.DTO.Request.UploadDocumentFileRequest;
import com.example.Axora.MVP.document.Entity.*;
import com.example.Axora.MVP.document.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentFileRepository fileRepository;
    private final DocumentPermissionRepository permissionRepository;
    private final DocumentExtractionJobRepository extractionJobRepository;

    /* ================= CREATE ================= */

    @Override
    public Document createDocument(CreateDocumentRequest request, UUID userId) {

        if (documentRepository.existsByWorkspaceIdAndTitle(
                request.getWorkspaceId(), request.getTitle())) {
            throw new IllegalStateException("Document title already exists");
        }

        Document document = Document.builder()
                .workspaceId(request.getWorkspaceId())
                .ownerId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .documentType(request.getDocumentType())
                .status("active")
                .build();

        document = documentRepository.save(document);

        permissionRepository.save(
                DocumentPermission.builder()
                        .documentId(document.getId())
                        .userId(userId)
                        .role("owner")
                        .build()
        );

        return document;
    }

    /* ================= UPLOAD / VERSION ================= */

    @Override
    public DocumentVersion uploadFile(
            UploadDocumentFileRequest request,
            UUID userId
    ) {
        Document document = validateEditAccess(request.getDocumentId(), userId);

        int nextVersion =
                versionRepository.findTopByDocumentIdOrderByVersionNumberDesc(
                                document.getId())
                        .map(v -> v.getVersionNumber() + 1)
                        .orElse(1);

        DocumentVersion version = DocumentVersion.builder()
                .document(document)
                .versionNumber(nextVersion)
                .createdBy(userId)
                .changeSummary("File upload")
                .build();

        version = versionRepository.save(version);

        DocumentFile file = DocumentFile.builder()
                .document(document)
                .version(version)
                .fileType(request.getFileType())
                .mimeType(request.getMimeType())
                .storageUrl(request.getStorageUrl())
                .sizeInBytes(request.getSizeInBytes())
                .checksum(request.getChecksum())
                .build();

        fileRepository.save(file);

        createExtractionJob(document.getId(), version.getId());

        return version;
    }

    /* ================= READ ================= */

    @Override
    @Transactional(readOnly = true)
    public List<DocumentVersion> getVersions(UUID documentId, UUID userId) {
        validateViewAccess(documentId, userId);
        return versionRepository.findByDocumentIdOrderByVersionNumberDesc(documentId);
    }

    /* ================= ARCHIVE ================= */

    @Override
    public void archiveDocument(UUID documentId, UUID userId) {
        Document document = validateOwnerAccess(documentId, userId);
        document.setStatus("archived");
        documentRepository.save(document);
    }

    /* ================= DELETE ================= */

    @Override
    public void deleteDocument(UUID documentId, UUID userId) {
        Document document = validateOwnerAccess(documentId, userId);
        document.setStatus("deleted");
        documentRepository.save(document);
    }

    /* ================= ACCESS CONTROL ================= */

    private Document validateViewAccess(UUID documentId, UUID userId) {
        return permissionRepository
                .findByDocumentIdAndUserId(documentId, userId)
                .map(p -> documentRepository.findById(documentId)
                        .orElseThrow(() -> new EntityNotFoundException("Document not found")))
                .orElseThrow(() -> new SecurityException("Access denied"));
    }

    private Document validateEditAccess(UUID documentId, UUID userId) {
        DocumentPermission perm =
                permissionRepository.findByDocumentIdAndUserId(documentId, userId)
                        .orElseThrow(() -> new SecurityException("Access denied"));

        if (!List.of("owner", "editor").contains(perm.getRole())) {
            throw new SecurityException("Edit access denied");
        }

        return documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
    }

    private Document validateOwnerAccess(UUID documentId, UUID userId) {
        DocumentPermission perm =
                permissionRepository.findByDocumentIdAndUserId(documentId, userId)
                        .orElseThrow(() -> new SecurityException("Owner access required"));

        if (!"owner".equals(perm.getRole())) {
            throw new SecurityException("Owner access required");
        }

        return documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
    }

    /* ================= EXTRACTION ================= */

    private void createExtractionJob(UUID documentId, UUID versionId) {
        extractionJobRepository.save(
                DocumentExtractionJob.builder()
                        .documentId(documentId)
                        .versionId(versionId)
                        .pipeline("default")
                        .status("pending")
                        .build()
        );
    }
}

