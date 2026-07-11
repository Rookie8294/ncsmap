package com.ncsmap.auth.security;

import com.ncsmap.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class LoginUser implements UserDetails, OAuth2User {

    private final Long memberId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public LoginUser(
            Long memberId,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes
    ) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static LoginUser from(Member member, Collection<? extends GrantedAuthority> authorities) {
        return new LoginUser(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                authorities,
                Map.of()
        );
    }

    public static LoginUser fromOAuth(Member member,
                                      Collection<? extends GrantedAuthority> authorities,
                                      Map<String, Object> attributes) {
        return new LoginUser(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                authorities,
                attributes
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getName() {
        return String.valueOf(memberId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
