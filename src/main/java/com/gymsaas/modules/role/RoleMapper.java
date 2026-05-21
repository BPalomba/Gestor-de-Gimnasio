package com.gymsaas.modules.role;

import com.gymsaas.modules.role.dto.PermissionResponse;
import com.gymsaas.modules.role.dto.RoleResponse;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setActive(role.isActive());
        response.setCreatedAt(role.getCreatedAt());
        response.setPermissions(role.getPermissions().stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toSet()));
        return response;
    }

    public PermissionResponse toPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setCode(permission.getCode());
        response.setDescription(permission.getDescription());
        response.setModule(permission.getModule());
        return response;
    }
}