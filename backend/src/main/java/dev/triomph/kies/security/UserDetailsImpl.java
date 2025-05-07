package dev.triomph.kies.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Player;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private boolean hasAccount;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String password, boolean hasAccount, 
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.hasAccount = hasAccount;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Player player) {
        Account account = player.getAccount();
        
        if (account != null) {
            List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(account.getRole())
            );
            
            return new UserDetailsImpl(
                player.getPlayerId(),
                player.getNickname(),
                account.getPassword(),
                true,
                authorities
            );
        } else {
            List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_PLAYER")
            );
            
            return new UserDetailsImpl(
                player.getPlayerId(),
                player.getNickname(),
                "",
                false,
                authorities
            );
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public boolean hasAccount() {
        return hasAccount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 