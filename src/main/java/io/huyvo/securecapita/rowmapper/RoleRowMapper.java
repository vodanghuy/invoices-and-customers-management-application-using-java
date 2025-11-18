package io.huyvo.securecapita.rowmapper;

import io.huyvo.securecapita.model.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role>{

    @Override
    public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .permissions(resultSet.getString("permissions"))
                .build();
    }
    /*
        Nhiệm vụ:
        - Lấy 1 dòng dữ liệu từ ResultSet (kết quả SELECT).
        - Chuyển dòng đó thành 1 object Role.
        Spring sẽ gọi phương thức này mỗi khi đọc được một dòng từ database.
     */
}
