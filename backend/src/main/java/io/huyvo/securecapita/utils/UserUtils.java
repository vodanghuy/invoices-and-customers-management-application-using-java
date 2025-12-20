package io.huyvo.securecapita.utils;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.model.UserPrincipal;
import org.springframework.security.core.Authentication;

public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserDTO) authentication.getPrincipal());
    }

    public static UserDTO getLoggedInUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }
}
