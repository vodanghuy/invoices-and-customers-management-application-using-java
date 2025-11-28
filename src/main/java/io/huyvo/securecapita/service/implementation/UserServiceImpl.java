package io.huyvo.securecapita.service.implementation;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.dtomapper.UserDTOMapper;
import io.huyvo.securecapita.model.User;
import io.huyvo.securecapita.repository.UserRepository;
import io.huyvo.securecapita.service.UserService;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return UserDTOMapper.fromUser(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO userDTO) {
        userRepository.sendVerificationCode(userDTO);
    }

}
