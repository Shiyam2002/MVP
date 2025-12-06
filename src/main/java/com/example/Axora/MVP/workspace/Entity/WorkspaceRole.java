package com.example.Axora.MVP.workspace.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "workspace_roles")
@Data
public class WorkspaceRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // SERIAL

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
}

