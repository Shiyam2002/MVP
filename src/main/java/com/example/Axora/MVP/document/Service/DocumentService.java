package com.example.Axora.MVP.document.Service;

import com.example.Axora.MVP.document.DTO.Request.CreateDocumentRequest;
import com.example.Axora.MVP.document.DTO.Request.UploadDocumentFileRequest;
import com.example.Axora.MVP.document.Entity.Document;
import com.example.Axora.MVP.document.Entity.DocumentVersion;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    Document createDocument(CreateDocumentRequest request, UUID userId);

    DocumentVersion uploadFile(
            UploadDocumentFileRequest request,
            UUID userId
    );

    List<DocumentVersion> getVersions(UUID documentId, UUID userId);

    void archiveDocument(UUID documentId, UUID userId);

    void deleteDocument(UUID documentId, UUID userId);
}

