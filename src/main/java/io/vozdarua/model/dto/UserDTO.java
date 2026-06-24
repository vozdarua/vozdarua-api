package io.vozdarua.model.dto;

import io.vozdarua.model.entity.User;

public record UserDTO(Long id, String phone, String email, String role) {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.id, user.phone, user.email, user.role);
    }
}
