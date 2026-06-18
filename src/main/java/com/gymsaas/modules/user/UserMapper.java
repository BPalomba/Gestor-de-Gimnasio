package com.gymsaas.modules.user;

import com.gymsaas.modules.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRoleName(user.getRole().getName());
        response.setRoleId(user.getRole().getId());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getBranch() != null) {
            response.setBranchName(user.getBranch().getName());
            response.setBranchId(user.getBranch().getId());
        }

        return response;
    }
}