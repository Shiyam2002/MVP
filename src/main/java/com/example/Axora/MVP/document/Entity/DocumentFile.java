package com.example.Axora.MVP.document.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentFile {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", nullable = false)
    private DocumentVersion version;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "mine_type", nullable = false)
    private String mimeType;

    @Column(name = "storage_url", nullable = false)
    private String storageUrl;

    @Column(name = "size_in_bytes")
    private Long sizeInBytes;

    private String checksum;

    @CreationTimestamp
    private Instant uploadedAt;
}
