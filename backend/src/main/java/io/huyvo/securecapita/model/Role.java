package io.huyvo.securecapita.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Ho tro ke thua giua lop cha - lop con
@SuperBuilder
// Chi dua vao JSON nhung field co gia tri khac voi gia tri mac dinh
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Role {
    private Long id;
    @NotEmpty(message = "Role name cannot be empty")
    private String name;
    @NotEmpty(message = "Permissions cannot be empty")
    private String permissions;
}
