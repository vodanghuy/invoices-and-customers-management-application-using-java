package io.huyvo.securecapita.service;

import io.huyvo.securecapita.model.Role;

public interface RoleService {
    Role getRoleByUserId(Long id);
}
