package com.example.Axora.MVP.document.Service;

import com.example.Axora.MVP.document.DTO.Request.PresignedUploadCompleteRequest;
import com.example.Axora.MVP.document.DTO.Request.PresignedUploadInitRequest;
import com.example.Axora.MVP.document.DTO.Response.PresignedUploadInitResponse;
import com.example.Axora.MVP.document.Entity.Document;
import com.example.Axora.MVP.document.Entity.DocumentFile;
import com.example.Axora.MVP.document.Entity.DocumentPermission;
import com.example.Axora.MVP.document.Entity.DocumentVersion;
import com.example.Axora.MVP.document.Repository.DocumentFileRepository;
import com.example.Axora.MVP.document.Repository.DocumentPermissionRepository;
import com.example.Axora.MVP.document.Repository.DocumentRepository;
import com.example.Axora.MVP.document.Repository.DocumentVersionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentUploadService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentFileRepository documentFileRepository;
    private final DocumentPermissionRepository documentPermissionRepository;
    private final MinioStorageService minioStorageService;

    @Value("${minio.bucket}")
    private String bucket;

    public PresignedUploadInitResponse initUpload(
            UUID documentId,
            PresignedUploadInitRequest request,
            UUID userId
    ) {
        validateEditAccess(documentId, userId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        int nextVersion =
                documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(documentId)
                        .map(v -> v.getVersionNumber() + 1)
                        .orElse(1);

        DocumentVersion version = documentVersionRepository.save(
                DocumentVersion.builder()
                        .document(document)
                        .versionNumber(nextVersion)
                        .createdBy(userId)
                        .changeSummary("file upload")
                        .build()
        );

        String objectKey = buildObjectKey(
                document.getWorkspaceId(),
                documentId,
                version.getVersionNumber(),
                request.getFileName()
        );

        String uploadUrl =
                minioStorageService.generatePresignedUploadUrl(objectKey, 15);

        return  PresignedUploadInitResponse.builder()
                .documentId(documentId)
                .versionId(version.getId())
                .objectKey(objectKey)
                .uploadUrl(uploadUrl)
                .expiresInMinutes(15)
                .build();
    }

    public void completeUpload(
            UUID documentId,
            PresignedUploadCompleteRequest request,
            UUID userId
    ) {
        validateEditAccess(documentId, userId);

        DocumentVersion version = documentVersionRepository.findById(request.getVersionId())
                .orElseThrow(() -> new EntityNotFoundException("Version not found"));

        DocumentFile file = DocumentFile.builder()
                .document(version.getDocument())
                .version(version)
                .fileType("original")
                .mimeType("application/octet-stream")
                .storageUrl("")
                .checksum(request.getChecksum())
                .build();

        documentFileRepository.save(file);
    }

    // Helper Functions

    private void validateEditAccess(UUID documentId, UUID userId) {
        DocumentPermission perm =
                documentPermissionRepository.findByDocumentIdAndUserId(documentId, userId)
                        .orElseThrow(() -> new SecurityException("Access denied"));

        if (!List.of("owner", "editor").contains(perm.getRole())) {
            throw new SecurityException("Edit access denied");
        }
    }

    private String buildObjectKey(
            UUID workspaceId,
            UUID documentId,
            int version,
            String fileName
    ) {
        return String.format(
                "workspace-%s/document-%s/v%d/%s",
                workspaceId,
                documentId,
                version,
                fileName
        );
    }
}
