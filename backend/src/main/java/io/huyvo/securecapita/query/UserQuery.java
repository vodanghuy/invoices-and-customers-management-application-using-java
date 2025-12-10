package io.huyvo.securecapita.query;

public class UserQuery {
    public static final String COUNT_USER_EMAIL_QUERY = "SELECT COUNT(*) FROM Users WHERE email = :email";
    public static final String INSERT_USER_QUERY = "INSERT INTO Users (first_name, last_name, email, password) VALUES (:firstName, :lastName, :email, :password)";
    public static final String INSERT_ACCOUNT_VERIFICATION_QUERY = "INSERT INTO AccountVerifications (user_id, url) VALUES (:userId, :url)";
    public static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM Users WHERE email = :email";
    public static final String DELETE_VERIFICATION_CODE_BY_USER_ID_QUERY = "DELETE FROM TwoFactorVerifications WHERE user_id = :userId";
    public static final String INSERT_VERIFICATION_CODE_QUERY = "INSERT INTO TwoFactorVerifications (user_id, code, expiration_date) VALUES (:userId, :verificationCode, :expirationDate)";
    public static final String SELECT_USER_BY_CODE_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM TwoFactorVerifications WHERE code = :code)";
    public static final String DELETE_CODE = "DELETE FROM TwoFactorVerifications WHERE code = :code";
    public static final String SELECT_CODE_EXPIRATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM TwoFactorVerifications WHERE code = :code";
    public static final String DELETE_PASSWORD_VERIFICATIONS_BY_USER_ID_QUERY = "DELETE FROM ResetPasswordVerifications WHERE user_id = :userId";
    public static final String INSERT_PASSWORD_VERIFICATIONS_QUERY = "INSERT INTO ResetPasswordVerifications (user_id, url, expiration_date) VALUES (:userId, :url, :expirationDate)";
    public static final String SELECT_RESET_PASSWORD_URL_EXPIRATION_QUERY = "SELECT expiration_date < NOW() AS is_expired FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_RESET_PASSWORD_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String UPDATE_USER_PASSWORD_BY_KEY = "UPDATE Users SET password = :password WHERE id = (SELECT user_id FROM ResetPasswordVerifications WHERE url = :url)";
    public static final String DELETE_PASSWORD_VERIFICATIONS_BY_URL_QUERY = "DELETE FROM ResetPasswordVerifications WHERE url = :url";
    public static final String SELECT_USER_BY_ACCOUNT_VERIFICATIONS_URL_QUERY = "SELECT * FROM Users WHERE id = (SELECT user_id FROM AccountVerifications WHERE url = :url)";
    public static final String UPDATE_USER_ENABLED_QUERY = "UPDATE Users SET enabled = :enabled WHERE id = :id";
}
