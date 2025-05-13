package dev.triomph.kies.controller;

import dev.triomph.kies.dto.AuthResponse;
import dev.triomph.kies.dto.LoginRequest;
import dev.triomph.kies.dto.PlayerRequest;
import dev.triomph.kies.dto.RegisterRequest;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://frontend:3000", "http://frontend"}, maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        Player player = authService.registerAccount(registerRequest);
        LoginRequest loginRequest = new LoginRequest(registerRequest.getEmail(), registerRequest.getPassword());
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/temp-player")
    public ResponseEntity<?> createTemporaryPlayer(@RequestBody PlayerRequest playerRequest) {
        Player newlyCreatedPlayer = authService.createTemporaryPlayer(playerRequest.getNickname());
        AuthResponse response = authService.authenticateTemporaryPlayer(newlyCreatedPlayer);
        return ResponseEntity.ok(response);
    }
} 