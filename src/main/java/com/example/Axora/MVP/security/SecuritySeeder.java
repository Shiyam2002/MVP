package com.example.Axora.MVP.security;

import com.example.Axora.MVP.security.Entity.*;
import com.example.Axora.MVP.security.Repository.PermissionRepository;
import com.example.Axora.MVP.security.Repository.RolePermissionRepository;
import com.example.Axora.MVP.security.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecuritySeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(String... args) {
        seedRoles();
        seedPermissions();
        seedRolePermissions();
    }

    // -----------------------------------------------------------------------
    // ROLES
    // -----------------------------------------------------------------------
    private void seedRoles() {

        Map<String, String> roles = Map.of(
                "SUPER_ADMIN", "Full platform-wide administrator",
                "OWNER", "Owner of workspace with full control",
                "ADMIN", "Administrator of workspace projects and members",
                "EDITOR", "Editor with document & project edit permissions",
                "VIEWER", "Read-only access to workspace content",
                "COMMENTER", "Permission to view & comment, but not edit content",
                "BOT", "Automated system role for background tasks"
        );

        roles.forEach((name, description) -> {
            roleRepository.findByName(name).orElseGet(() -> {
                Role role = new Role();
                role.setName(name);
                role.setDescription(description);
                return roleRepository.save(role);
            });
        });
    }

    // -----------------------------------------------------------------------
    // PERMISSIONS
    // -----------------------------------------------------------------------
    private void seedPermissions() {

        Map<String, String> permissions = new LinkedHashMap<>();

        // Platform permissions
        permissions.put("platform.manage", "Full platform control (super admin)");
        permissions.put("workspace.view_all", "View all workspaces across platform");
        permissions.put("workspace.create", "Create a new workspace");
        permissions.put("workspace.delete", "Delete workspace");
        permissions.put("workspace.billing.manage", "Manage workspace billing & subscription");

        // Workspace member & role management
        permissions.put("workspace.members.invite", "Invite new members into workspace");
        permissions.put("workspace.members.remove", "Remove members from workspace");
        permissions.put("workspace.roles.change", "Assign or change users' workspace roles");

        // Project permissions
        permissions.put("project.create", "Create new project inside workspace");
        permissions.put("project.delete", "Delete a project");
        permissions.put("project.invite", "Invite users to project");
        permissions.put("project.edit_settings", "Edit project settings");
        permissions.put("project.view", "View project details");
        permissions.put("project.visibility.change", "Change project public/private visibility");
        permissions.put("project.archive", "Archive project");
        permissions.put("project.restore", "Restore archived project");

        // Document permissions
        permissions.put("document.upload", "Upload documents");
        permissions.put("document.edit", "Edit documents");
        permissions.put("document.delete", "Delete documents");
        permissions.put("document.download", "Download documents");
        permissions.put("document.annotate", "Annotate documents");

        // Comments
        permissions.put("comment.add", "Add comments");
        permissions.put("comment.view", "View comments");
        permissions.put("comment.delete", "Delete comments");

        // AI permissions
        permissions.put("ai.tools.run", "Run AI tools like OCR, summarize, embed");
        permissions.put("ai.model.manage", "Manage internal AI models or pipelines");

        // Analytics
        permissions.put("analytics.view", "View workspace analytics");
        permissions.put("analytics.own_actions", "View analytics only about own actions");

        // Workflows
        permissions.put("workflow.automate", "Create task automations and workflows");
        permissions.put("workflow.background_jobs", "Run automated background tasks");

        // System
        permissions.put("system.config", "Access to system configuration");
        permissions.put("system.health_monitor", "Access to system health dashboard");

        // Bot Permissions
        permissions.put("bot.auto_upload", "Bot can automatically upload documents");
        permissions.put("bot.document.edit", "Bot can modify documents automatically");
        permissions.put("bot.run_jobs", "Bot can execute background jobs");

        // Insert if not exists
        permissions.forEach((code, description) -> {
            permissionRepository.findByCode(code).orElseGet(() -> {
                Permission p = new Permission();
                p.setCode(code);
                p.setDescription(description);
                return permissionRepository.save(p);
            });
        });
    }

    // -----------------------------------------------------------------------
    // ROLE → PERMISSION MAPPING
    // -----------------------------------------------------------------------
    private void seedRolePermissions() {

        // Load all permissions for convenience
        Map<String, Permission> allPermissions = new HashMap<>();
        permissionRepository.findAll().forEach(p -> allPermissions.put(p.getCode(), p));

        // -----------------------------
        // ROLE → PERMISSIONS MAP
        // -----------------------------
        Map<String, List<String>> mapping = new HashMap<>();

        // SUPER_ADMIN gets ALL permissions
        mapping.put("SUPER_ADMIN", new ArrayList<>(allPermissions.keySet()));

        mapping.put("OWNER", List.of(
                "workspace.create", "workspace.delete",
                "workspace.members.invite", "workspace.members.remove",
                "workspace.roles.change", "workspace.billing.manage",

                "project.create", "project.delete", "project.invite",
                "project.edit_settings", "project.view",
                "project.visibility.change", "project.archive", "project.restore",

                "document.upload", "document.edit", "document.delete",
                "document.download", "document.annotate",

                "comment.add", "comment.view", "comment.delete",

                "ai.tools.run", "analytics.view",
                "workflow.automate", "workflow.background_jobs"
        ));

        mapping.put("ADMIN", List.of(
                "project.create", "project.delete", "project.invite",
                "project.edit_settings", "project.view",

                "document.upload", "document.edit", "document.delete",
                "document.download", "document.annotate",

                "comment.add", "comment.view",

                "ai.tools.run", "analytics.view", "workflow.automate"
        ));

        mapping.put("EDITOR", List.of(
                "project.view",
                "document.upload", "document.edit", "document.download", "document.annotate",
                "comment.add", "comment.view",
                "ai.tools.run"
        ));

        mapping.put("VIEWER", List.of(
                "project.view",
                "comment.view",
                "document.download"
        ));

        mapping.put("COMMENTER", List.of(
                "comment.add",
                "comment.view",
                "document.download"
        ));

        mapping.put("BOT", List.of(
                "bot.auto_upload",
                "bot.document.edit",
                "bot.run_jobs"
        ));

        // -------------------------------------------------------------------
        // INSERT MAPPINGS
        // -------------------------------------------------------------------
        for (var entry : mapping.entrySet()) {

            String roleName = entry.getKey();
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role missing: " + roleName));

            List<String> PermissionCodes = entry.getValue();

            for (String code : PermissionCodes) {

                Permission permission = allPermissions.get(code);

                RolePermissionId id = new RolePermissionId(role.getId(), permission.getId());

                // Avoid duplicates
                rolePermissionRepository.findById(id).orElseGet(() -> {
                    RolePermission rp = new RolePermission();
                    rp.setId(id);
                    rp.setRole(role);
                    rp.setPermission(permission);
                    return rolePermissionRepository.save(rp);
                });
            }
        }
    }
}

