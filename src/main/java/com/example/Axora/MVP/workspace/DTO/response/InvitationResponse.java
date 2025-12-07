package com.example.Axora.MVP.workspace.DTO.response;

import lombok.Data;
import java.util.UUID;

@Data
public class InvitationResponse {

    private UUID id;
    private String email;
    private String status;
    private String token;
}
