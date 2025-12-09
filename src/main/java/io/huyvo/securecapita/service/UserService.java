package io.huyvo.securecapita.service;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.User;
import jakarta.validation.constraints.NotEmpty;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    UserDTO verifyCode(String email, String code);

    void resetPassword(String email);

    UserDTO verifyPasswordKey(String key);

    void resetPasswordWithKey(String key, String password, String confirmPassword);
}
