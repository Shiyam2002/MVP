package com.example.Axora.MVP.document.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UploadDocumentFileRequest {
    private UUID documentId;
    private String fileType;
    private String mimeType;
    private String storageUrl;
    private Long sizeInBytes;
    private String checksum;
}

