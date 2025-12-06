package com.example.Axora.MVP.workspace.Exception.Workspace;

public class WorkspaceAlreadyExistException extends RuntimeException {
    public WorkspaceAlreadyExistException(String message) {
        super(message);
    }
}
