package io.huyvo.securecapita.dto;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private Boolean enabled;
    private Boolean isNotLocked;
    private Boolean isUsingMfa;
    private LocalDateTime createdAt;
    private String roleName;
    private String permissions;
}
