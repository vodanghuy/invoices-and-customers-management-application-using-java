package io.huyvo.securecapita.repository;

import io.huyvo.securecapita.model.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role>{
    /* Basic CRUD Operations */
    T create(T data);
    Collection<T> list(int page, int pageSize);
    T update(T data);
    void delete(Long id);

    /* More Complex Operations */
    void addRoleToUser(Long userId, String roleName);
}
