package com.example.Axora.MVP.document.DTO.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@Builder
public class PresignedUploadInitResponse {
    private UUID documentId;
    private UUID versionId;
    private String objectKey;
    private String uploadUrl;
    private Integer expiresInMinutes;
}
