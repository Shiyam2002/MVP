package com.example.Axora.MVP.user.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Data
public class Permission {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String code;

    private String description;
}

