package io.huyvo.securecapita.service;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.User;
import jakarta.validation.constraints.NotEmpty;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);
}
