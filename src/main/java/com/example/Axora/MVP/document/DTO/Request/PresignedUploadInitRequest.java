package com.example.Axora.MVP.document.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PresignedUploadInitRequest {
    @NotBlank
    private String fileName;

    @NotBlank
    private String fileType;

    @NotBlank
    private String mimeType;

    @NotNull
    private Long sizeInBytes;
}
