package io.huyvo.securecapita.service;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.User;

public interface UserService {
    UserDTO createUser(User user);
}
