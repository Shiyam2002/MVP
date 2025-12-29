package com.example.Axora.MVP.document.Route.V1;

import com.example.Axora.MVP.document.DTO.Request.PresignedUploadCompleteRequest;
import com.example.Axora.MVP.document.DTO.Request.PresignedUploadInitRequest;
import com.example.Axora.MVP.document.DTO.Response.PresignedUploadInitResponse;
import com.example.Axora.MVP.document.Service.DocumentUploadService;
import com.example.Axora.MVP.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentUploadController {

    private final DocumentUploadService uploadService;

    /* ================= INIT ================= */

    @PostMapping("/{documentId}/upload/init")
    public ResponseEntity<PresignedUploadInitResponse> initUpload(
            @PathVariable UUID documentId,
            @RequestBody @Valid PresignedUploadInitRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUser();
        return ResponseEntity.ok(
                uploadService.initUpload(documentId, request, userId)
        );
    }

    /* ================= COMPLETE ================= */

    @PostMapping("/{documentId}/upload/complete")
    public ResponseEntity<Void> completeUpload(
            @PathVariable UUID documentId,
            @RequestBody @Valid PresignedUploadCompleteRequest request
    ) {
        UUID userId = SecurityUtils.getCurrentUser();
        uploadService.completeUpload(documentId, request, userId);
        return ResponseEntity.noContent().build();
    }
}

