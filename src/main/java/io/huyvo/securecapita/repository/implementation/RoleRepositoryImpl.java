package io.huyvo.securecapita.repository.implementation;

import io.huyvo.securecapita.exception.ApiException;
import io.huyvo.securecapita.model.Role;
import io.huyvo.securecapita.repository.RoleRepository;
import io.huyvo.securecapita.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.huyvo.securecapita.enumeration.RoleType.ROLE_USER;
import static io.huyvo.securecapita.query.RoleQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Add role {} to user id {}", roleName, userId);
        try{

        }catch (EmptyResultDataAccessException exception)
        {
            throw new ApiException("No role found by name: " + roleName);
        }catch (Exception ex){
            throw new ApiException("An error occurred!");
        }
        Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
        // RowMapper: là interface giúp Spring chuyển từng dòng dữ liệu lấy từ database thành một đối tượng Java.
        jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", role.getId()));
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
