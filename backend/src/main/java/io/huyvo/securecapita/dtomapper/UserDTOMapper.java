package io.huyvo.securecapita.dtomapper;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.Role;
import io.huyvo.securecapita.model.User;
import org.springframework.beans.BeanUtils;

public class UserDTOMapper {

    public static UserDTO fromUser(User user){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermissions());
        return userDTO;
    }

    public static User toUser(UserDTO userDTO){
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
