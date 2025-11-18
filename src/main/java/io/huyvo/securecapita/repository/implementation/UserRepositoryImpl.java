package io.huyvo.securecapita.repository.implementation;

import io.huyvo.securecapita.exception.ApiException;
import io.huyvo.securecapita.model.Role;
import io.huyvo.securecapita.model.User;
import io.huyvo.securecapita.repository.RoleRepository;
import io.huyvo.securecapita.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.huyvo.securecapita.enumeration.RoleType.*;
import static io.huyvo.securecapita.query.UserQuery.COUNT_USER_EMAIL_QUERY;
import static io.huyvo.securecapita.query.UserQuery.INSERT_USER_QUERY;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {

    private final NamedParameterJdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder;
    /*
        Dung de truy van database bang ten tham so (VD: :username, :bio,...)
        VD: String sql = "SELECT * FROM users WHERE username = :username";
        Mac dinh: String sql = "SELECT * FROM users WHERE username = ?";
     */

    private final RoleRepository<Role> roleRepository;

    @Override
    public User create(User user) {
        // Check the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase()) > 0){
            throw new ApiException("Email already in user. Please use a different email and try again");
        }
        try{
            // Khoi tao KeyHolder de lay ID tao ra tu database
            KeyHolder keyHolder = new GeneratedKeyHolder();
            // Lay cac tham so SQL tu object user
            SqlParameterSource parameter = getSqlParameterSource(user);
            // Insert user vao database va tra ve ID vua tao
            jdbc.update(INSERT_USER_QUERY, parameter, keyHolder);
            // Gan ID vua duoc tao vao doi tuong user
            user.setId(requireNonNull(keyHolder.getKey()).longValue());
            // Add role to user
            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
        }
        catch (Exception e)
        {
            throw new ApiException("An error occurred!");
        }
        return null;
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private int getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
        /*
            - Map.of("email", email): tao mot Map chua 1 cap key - value:
              + Key: "email" -> ten duoc su dung trong SQL (:email)
              + Value: email -> gia tri email duoc truyen vao
            - queryForObject(...): chay cau SQL va lay dung 1 gia tri duy nhat. Yeu cau
              tra ve Integer.class, tuc ket qua se la so nguyen.
         */
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getFirstName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }
}