package com.example.Axora.MVP.workspace.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "workspace_permissions")
@Data
public class WorkspacePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // SERIAL

    @Column(unique = true, nullable = false)
    private String code;

    private String description;
}

