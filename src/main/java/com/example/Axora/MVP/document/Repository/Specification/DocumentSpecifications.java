package com.example.Axora.MVP.document.Repository.Specification;

import com.example.Axora.MVP.document.Entity.Document;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class DocumentSpecifications {

    private DocumentSpecifications() {}

    public static Specification<Document> hasWorkspace(UUID workspaceId) {
        return (root, query, cb) ->
                cb.equal(root.get("workspaceId"), workspaceId);
    }

    public static Specification<Document> hasOwner(UUID ownerId) {
        return (root, query, cb) ->
                cb.equal(root.get("ownerId"), ownerId);
    }

    public static Specification<Document> hasStatus(String status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Document> hasType(String documentType) {
        return (root, query, cb) ->
                cb.equal(root.get("documentType"), documentType);
    }

    public static Specification<Document> titleContains(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")),
                        "%" + keyword.toLowerCase() + "%");
    }
}
