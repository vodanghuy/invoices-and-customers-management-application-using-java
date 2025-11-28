package io.huyvo.securecapita.repository;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.User;

import java.util.Collection;

public interface UserRepository<T extends User>{
    /* Basic CRUD Operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete(Long id);

    T getUserByEmail(String email);

    void sendVerificationCode(UserDTO userDTO);

    /* More Complex Operations */
}
