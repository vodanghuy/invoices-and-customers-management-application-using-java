package io.huyvo.securecapita.repository.implementation;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.enumeration.VerificationType;
import io.huyvo.securecapita.exception.ApiException;
import io.huyvo.securecapita.model.*;
import io.huyvo.securecapita.repository.*;
import io.huyvo.securecapita.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.EmptyResultDataAccessException;
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
import static io.huyvo.securecapita.utils.SmsUtils.sendSms;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
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
        try{
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("No user found by email: " + email);
        }
        catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
        String verificationCode = randomAlphabetic(8).toUpperCase();
        try{
            jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY, Map.of("userId", userDTO.getId()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of("userId", userDTO.getId(), "verificationCode", verificationCode, "expirationDate", expirationDate));
            //sendSms(userDTO.getPhone(), "From SecureCapita:\nVerification Code\n" + verificationCode);
            log.info("Verification code: {}", verificationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public User verifyCode(String email, String code) {
        if(isVerificationCodeExpired(code)){
            throw new ApiException("This code has expired. Please login again");
        }
        try{
            User userByCode = jdbc.queryForObject(SELECT_USER_BY_CODE_QUERY, Map.of("code", code), new UserRowMapper());
            User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
            if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())){
                jdbc.update(DELETE_CODE, Map.of("code", code));
                return userByCode;
            }
            else{
                throw new ApiException("Invalid code. Please try again!");
            }
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Could not find the record");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public void resetPassword(String email) {
        if(getEmailCount(email.trim().toLowerCase()) <= 0){
            throw new ApiException("There is no account with this email address");
        }
        try{
            String expirationDate = DateFormatUtils.format(addDays(new Date(), 1), DATE_FORMAT);
            String url = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
            User user = getUserByEmail(email);
            jdbc.update(DELETE_PASSWORD_VERIFICATIONS_BY_USER_ID_QUERY, Map.of("userId", user.getId()));
            jdbc.update(INSERT_PASSWORD_VERIFICATIONS_QUERY, Map.of("userId", user.getId(), "url", url, "expirationDate", expirationDate));
            log.info("Verification URL: {}", url);
        }catch (Exception exception){
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verifyPasswordKey(String key) {
        if(isResetPasswordVerificationUrlExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please reset your password again");
        try{
            return jdbc.queryForObject(SELECT_USER_BY_RESET_PASSWORD_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("This link is invalid. Please reset your password again!");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public void resetPasswordWithKey(String key, String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");
        try{
            jdbc.update(UPDATE_USER_PASSWORD_BY_KEY, Map.of("password", encoder.encode(password) ,"url", getVerificationUrl(key, PASSWORD.getType())));
            jdbc.update(DELETE_PASSWORD_VERIFICATIONS_BY_URL_QUERY, Map.of("url", getVerificationUrl(key, PASSWORD.getType())));
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public User verifyAccount(String key) {
        try{
            User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_VERIFICATIONS_URL_QUERY, Map.of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
            jdbc.update(UPDATE_USER_ENABLED_QUERY, Map.of("enabled", true, "id", user.getId()));
            return user;
        }catch (EmptyResultDataAccessException exception){
            log.error(exception.getMessage());
            throw new ApiException("This link is invalid.");
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    private Boolean isResetPasswordVerificationUrlExpired(String key, VerificationType password) {
        try{
            return jdbc.queryForObject(SELECT_RESET_PASSWORD_URL_EXPIRATION_QUERY, Map.of("url", getVerificationUrl(key, password.getType())), Boolean.class);
        }catch (EmptyResultDataAccessException e){
            log.error(e.getMessage());
            throw new ApiException("This link is invalid. Please reset your password again!");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    private Boolean isVerificationCodeExpired(String code) {
        try{
            return jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, Map.of("code", code), Boolean.class);
        }catch (EmptyResultDataAccessException e){
            throw new ApiException("Invalid code. Please login again!");
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("An error occurred");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }else {
            log.info("User found in the database: {}", email);
            return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
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