package com.example.Axora.MVP.document.DTO.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDocumentRequest {
    private UUID workspaceId;
    private String title;
    private String description;
    private String documentType;
}

