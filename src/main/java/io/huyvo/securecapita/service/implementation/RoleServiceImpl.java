package io.huyvo.securecapita.service.implementation;

import io.huyvo.securecapita.model.Role;
import io.huyvo.securecapita.repository.RoleRepository;
import io.huyvo.securecapita.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }
}
