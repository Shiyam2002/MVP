package com.example.Axora.MVP.document.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class PresignedUploadCompleteRequest {

    @NotNull
    private UUID versionId;

    @NotBlank
    private String objectKey;

    private String checksum;
}
