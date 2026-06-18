package com.gymsaas.modules.user;

import com.gymsaas.modules.branch.Branch;
import com.gymsaas.modules.branch.BranchRepository;
import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.modules.role.Role;
import com.gymsaas.modules.role.RoleRepository;
import com.gymsaas.modules.user.dto.*;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository       userRepository;
    private final RoleRepository       roleRepository;
    private final GymRepository        gymRepository;
    private final BranchRepository     branchRepository;
    private final UserMapper           mapper;
    private final PasswordEncoder      passwordEncoder;


    public List<UserResponse> findAll(UUID gymId) {
        return userRepository.findByGymIdOrderByLastNameAsc(gymId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public UserResponse findById(UUID gymId, UUID userId) {
        return userRepository.findByIdAndGymId(userId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserResponse create(UUID gymId, CreateUserRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario con ese email");
        }

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new BusinessException(
                        "Gimnasio no encontrado", HttpStatus.NOT_FOUND));

        Role role = roleRepository.findByIdAndGymId(req.getRoleId(), gymId)
                .orElseThrow(() -> new BusinessException(
                        "Rol no encontrado", HttpStatus.NOT_FOUND));

        User user = new User();
        user.setGym(gym);
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        user.setActive(true);

        if (req.getBranchId() != null) {
            Branch branch = branchRepository
                    .findByIdAndGymId(req.getBranchId(), gymId)
                    .orElseThrow(() -> new BusinessException(
                            "Sucursal no encontrada", HttpStatus.NOT_FOUND));
            user.setBranch(branch);
        }

        return mapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID gymId, UUID userId, UpdateUserRequest req) {
        User user = userRepository.findByIdAndGymId(userId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (req.getEmail() != null
                && !req.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario con ese email");
        }

        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName()  != null) user.setLastName(req.getLastName());
        if (req.getEmail()     != null) user.setEmail(req.getEmail());

        if (req.getRoleId() != null) {
            Role role = roleRepository.findByIdAndGymId(req.getRoleId(), gymId)
                    .orElseThrow(() -> new BusinessException(
                            "Rol no encontrado", HttpStatus.NOT_FOUND));
            user.setRole(role);
        }

        if (req.getBranchId() != null) {
            Branch branch = branchRepository
                    .findByIdAndGymId(req.getBranchId(), gymId)
                    .orElseThrow(() -> new BusinessException(
                            "Sucursal no encontrada", HttpStatus.NOT_FOUND));
            user.setBranch(branch);
        }

        return mapper.toResponse(user);
    }

    @Transactional
    public void changePassword(UUID gymId, UUID userId,
                               ChangePasswordRequest req) {
        User user = userRepository.findByIdAndGymId(userId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(req.getCurrentPassword(),
                user.getPasswordHash())) {
            throw new BusinessException(
                    "La contraseña actual es incorrecta");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
    }

    @Transactional
    public void deactivate(UUID gymId, UUID userId,
                           UUID authenticatedUserId) {

        if (userId.equals(authenticatedUserId)) {
            throw new BusinessException(
                    "No podés desactivar tu propio usuario");
        }

        User user = userRepository.findByIdAndGymId(userId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException("El usuario ya está desactivado");
        }
        user.setActive(false);
    }

    @Transactional
    public void reactivate(UUID gymId, UUID userId) {
        User user = userRepository.findByIdAndGymId(userId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (user.isActive()) {
            throw new BusinessException("El usuario ya está activo");
        }
        user.setActive(true);
    }
}