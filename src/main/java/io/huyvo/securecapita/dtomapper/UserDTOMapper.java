package io.huyvo.securecapita.dtomapper;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
/*
    - là một annotation của Spring dùng để đánh dấu một class là bean để
      Spring Boot tự động phát hiện và quản lý nó.
    - Spring sẽ đưa đối tượng đó vào IoC Container.
    - Sau đó bạn có thể @Autowired hoặc inject nó vào bất kỳ nơi nào.v
 */
public class UserDTOMapper {

    public static UserDTO fromUser(User user){
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static User toUser(UserDTO userDTO){
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
