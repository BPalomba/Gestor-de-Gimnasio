package com.gymsaas.modules.role;

import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.modules.role.dto.CreateRoleRequest;
import com.gymsaas.modules.role.dto.PermissionResponse;
import com.gymsaas.modules.role.dto.RoleResponse;
import com.gymsaas.modules.role.dto.UpdateRoleRequest;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository       roleRepository;
    private final PermissionRepository permissionRepository;
    private final GymRepository        gymRepository;
    private final RoleMapper           mapper;

    public List<RoleResponse> findAll(UUID gymId) {
        return roleRepository.findByGymIdAndActiveTrue(gymId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<PermissionResponse> findAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc()
                .stream()
                .map(mapper::toPermissionResponse)
                .toList();
    }

    public RoleResponse findById(UUID gymId, UUID roleId) {
        return roleRepository.findByIdAndGymId(roleId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Rol no encontrado", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public RoleResponse create(UUID gymId, CreateRoleRequest req) {

        if (roleRepository.existsByGymIdAndName(gymId, req.getName())) {
            throw new BusinessException(
                    "Ya existe un rol con ese nombre en este gimnasio");
        }

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new BusinessException(
                        "Gimnasio no encontrado", HttpStatus.NOT_FOUND));

        Set<Permission> permissions = resolvePermissions(req.getPermissionIds());

        Role role = new Role();
        role.setGym(gym);
        role.setName(req.getName());
        role.setDescription(req.getDescription());
        role.setPermissions(permissions);

        return mapper.toResponse(roleRepository.save(role));
    }

    @Transactional
    public RoleResponse update(UUID gymId, UUID roleId, UpdateRoleRequest req) {
        Role role = roleRepository.findByIdAndGymId(roleId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Rol no encontrado", HttpStatus.NOT_FOUND));

        if (req.getName() != null
                && !req.getName().equals(role.getName())
                && roleRepository.existsByGymIdAndName(gymId, req.getName())) {
            throw new BusinessException(
                    "Ya existe un rol con ese nombre en este gimnasio");
        }

        if (req.getName()        != null) role.setName(req.getName());
        if (req.getDescription() != null) role.setDescription(req.getDescription());
        if (req.getActive()      != null) role.setActive(req.getActive());
        if (req.getPermissionIds() != null) {
            role.setPermissions(resolvePermissions(req.getPermissionIds()));
        }

        return mapper.toResponse(role);
    }

    private Set<Permission> resolvePermissions(Set<UUID> ids) {
        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(ids));
        if (permissions.size() != ids.size()) {
            throw new BusinessException(
                    "Uno o más permisos indicados no existen");
        }
        return permissions;
    }
}