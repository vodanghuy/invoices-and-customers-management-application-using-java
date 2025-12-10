package io.huyvo.securecapita.model;

import io.huyvo.securecapita.dto.UserDTO;
import io.huyvo.securecapita.dtomapper.UserDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.role.getPermissions().split(",".trim())).map(SimpleGrantedAuthority::new).collect(toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.getIsNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getEnabled();
    }

    public UserDTO getUser(){
        return UserDTOMapper.fromUser(this.user, this.role);
    }
}
//Quy trình hoạt động có UserPrincipal
//
//Người dùng gửi email + password
//
//Spring gọi UserDetailsService.loadUserByUsername()
//
//Bạn tìm user trong DB
//
//Trả về new UserPrincipal(user)
//
//Spring dùng UserPrincipal để kiểm tra password
//
//Nếu hợp lệ → set vào SecurityContext
