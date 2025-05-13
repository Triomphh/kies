package dev.triomph.kies.service;

import dev.triomph.kies.DAO.AccountDAO;
import dev.triomph.kies.DAO.PlayerRepository;
import dev.triomph.kies.dto.AuthResponse;
import dev.triomph.kies.dto.LoginRequest;
import dev.triomph.kies.dto.RegisterRequest;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public Player createTemporaryPlayer(String nickname) {
        Player player = new Player(nickname);
        return playerRepository.save(player);
    }

    public AuthResponse authenticateTemporaryPlayer(Player player) {
        String jwt = jwtUtils.generateJwtToken(player.getPlayerId(), player.getNickname(), false, "ROLE_PLAYER");

        return new AuthResponse(
                jwt,
                player.getPlayerId(),
                player.getNickname(),
                false,
                "ROLE_PLAYER"
        );
    }

    @Transactional
    public Player registerAccount(RegisterRequest registerRequest) {
        Player player;
        
        if (registerRequest.getPlayerId() != null) {
            player = playerRepository.findById(registerRequest.getPlayerId())
                    .orElseThrow(() -> new RuntimeException("Error: Player not found with id: " + registerRequest.getPlayerId()));
        } else {
            player = new Player(registerRequest.getNickname());
        }
        
        if (player.getAccount() != null) {
            throw new RuntimeException("Error: Player already has an account!");
        }

        Account account = new Account(
                player,
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword()),
                registerRequest.getAge(),
                registerRequest.getGender()
        );
        
        if (registerRequest.getProfileImageUrl() != null && !registerRequest.getProfileImageUrl().isEmpty()) {
            account.setProfileImageUrl(registerRequest.getProfileImageUrl());
        }
        
        player.setAccount(account);
        return playerRepository.save(player);
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Account account = accountDAO.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found with email: " + loginRequest.getEmail()));
        
        Player player = account.getPlayer();
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(player.getPlayerId().toString(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        String role = account.getRole();

        return new AuthResponse(
                jwt,
                player.getPlayerId(),
                player.getNickname(),
                true,
                role
        );
    }
} 