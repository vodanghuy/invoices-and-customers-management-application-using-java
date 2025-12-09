package io.huyvo.securecapita.resource;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.dtomapper.UserDTOMapper;
import io.huyvo.securecapita.exception.ApiException;
import io.huyvo.securecapita.form.LoginForm;
import io.huyvo.securecapita.model.*;
import io.huyvo.securecapita.provider.TokenProvider;
import io.huyvo.securecapita.service.RoleService;
import io.huyvo.securecapita.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static io.huyvo.securecapita.utils.ExceptionUtils.processError;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RoleService roleService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm){
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = getAuthenticatedUser(authentication);
        return user.getIsUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user){
        UserDTO userDTO = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .data(Map.of("user", userDTO))
                        .message("User created successfully")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build());
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code){
        UserDTO user = userService.verifyCode(email, code);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .message("Login successfully")
                        .data(Map.of("user", user,
                                "accessToken", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refreshToken", tokenProvider.createRefreshToken(getUserPrincipal(user))
                        ))
                        .build()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .message("Profile Retrieved")
                        .data(Map.of("user", user))
                        .build()
        );
    }

    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email){
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .message("Email sent. Please check your email to reset password.")
                        .build()
        );
    }

    private UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private Authentication authenticate(String email, String password){
        try{
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email,password));
            return authentication;
        }catch (Exception exception){
            processError(request,response,exception);
            throw new ApiException(exception.getMessage());
        }
    }

    private URI getUri(){
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .message("Login successfully")
                        .data(Map.of("user", user,
                                "accessToken", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refreshToken", tokenProvider.createRefreshToken(getUserPrincipal(user))
                        ))
                        .build()
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(UserDTOMapper.toUser(userService.getUserByEmail(user.getEmail())), roleService.getRoleByUserId(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO userDTO) {
        userService.sendVerificationCode(userDTO);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .statusCode(HttpStatus.OK.value())
                        .status(HttpStatus.OK)
                        .message("Verification code sent.")
                        .data(Map.of("user", userDTO))
                        .build()
        );
    }
}
