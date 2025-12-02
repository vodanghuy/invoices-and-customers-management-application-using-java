package io.huyvo.securecapita.service.implementation;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.Role;
import io.huyvo.securecapita.model.User;
import io.huyvo.securecapita.repository.RoleRepository;
import io.huyvo.securecapita.repository.UserRepository;
import io.huyvo.securecapita.service.UserService;
import lombok.*;
import org.springframework.stereotype.Service;

import static io.huyvo.securecapita.dtomapper.UserDTOMapper.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        userRepository.sendVerificationCode(userDTO);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email, code));
    }

    private UserDTO mapToUserDTO(User user){
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
