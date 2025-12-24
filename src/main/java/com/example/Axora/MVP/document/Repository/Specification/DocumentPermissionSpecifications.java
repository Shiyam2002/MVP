package com.example.Axora.MVP.document.Repository.Specification;

import com.example.Axora.MVP.document.Entity.Document;
import com.example.Axora.MVP.document.Entity.DocumentPermission;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class DocumentPermissionSpecifications {

    private DocumentPermissionSpecifications() {}

    public static Specification<DocumentPermission> forUser(UUID userId) {
        return (root, query, cb) ->
                cb.equal(root.get("userId"), userId);
    }

    public static Specification<DocumentPermission> withRole(String role) {
        return (root, query, cb) ->
                cb.equal(root.get("role"), role);
    }

    public static Specification<Document> accessibleByUser(UUID userId) {
        return (root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<DocumentPermission> permRoot = subquery.from(DocumentPermission.class);

            subquery.select(permRoot.get("documentId"))
                    .where(cb.equal(permRoot.get("userId"), userId));

            return cb.in(root.get("id")).value(subquery);
        };
    }

}

