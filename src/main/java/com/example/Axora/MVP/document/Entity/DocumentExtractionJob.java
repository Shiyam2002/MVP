package com.example.Axora.MVP.document.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_extraction_jobs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentExtractionJob {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "version_id", nullable = false)
    private UUID versionId;

    private String pipeline;

    private String status;

    private Instant startedAt;
    private Instant completedAt;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;
}
