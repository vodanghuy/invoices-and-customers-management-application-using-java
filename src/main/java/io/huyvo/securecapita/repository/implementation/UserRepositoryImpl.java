package io.huyvo.securecapita.repository.implementation;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.exception.ApiException;
import io.huyvo.securecapita.model.*;
import io.huyvo.securecapita.repository.*;
import io.huyvo.securecapita.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static io.huyvo.securecapita.enumeration.RoleType.*;
import static io.huyvo.securecapita.enumeration.VerificationType.*;
import static io.huyvo.securecapita.query.UserQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

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
            // Send verification URL
            String verificationURL = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
            // Save URL in AccountVerifications table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_QUERY, Map.of("userId", user.getId(), "url", verificationURL));
            user.setEnabled(false);
            user.setIsNotLocked(true);
            return user;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            // It tells us where the error occurring
            throw new ApiException("An error occurred!");
        }
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

    @Override
    public User getUserByEmail(String email) {
        return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()).getPermissions());
        }
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

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/"+type+"/"+key).toUriString();
        /*
            - ServletUriComponentsBuilder.fromCurrentContextPath():
              + Lấy domain + port hiện tại của server.
              + Ví dụ, nếu API đang chạy tại: http://localhost:8080
            - .path("/user/verify/" + type + "/" + key): nối thêm phần đường dẫn (path) vào URL.
         */
    }
}