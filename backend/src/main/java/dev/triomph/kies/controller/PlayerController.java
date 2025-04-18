package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.service.PlayerService;


@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<Player> getPlayerByNickname(@PathVariable String nickname) {
        return playerService.getPlayerByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
        
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if player with nickname already exists
        if (playerService.getPlayerByNickname(nickname).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Player player = playerService.createPlayer(nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        if (!playerService.getPlayerById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        player.setPlayerId(id); // Ensure ID is set correctly
        return ResponseEntity.ok(playerService.updatePlayer(player));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        if (!playerService.getPlayerById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/increment-games")
    public ResponseEntity<Player> incrementGamesPlayed(@PathVariable Long id) {
        try {
            Player updatedPlayer = playerService.incrementGamesPlayed(id);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/increment-victories")
    public ResponseEntity<Player> incrementVictories(@PathVariable Long id) {
        try {
            Player updatedPlayer = playerService.incrementVictories(id);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}