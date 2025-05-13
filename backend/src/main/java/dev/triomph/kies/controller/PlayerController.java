package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.dto.PlayerDTO;
import dev.triomph.kies.dto.PlayerNicknameUpdateRequest;
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
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers()
                .stream()
                .map(PlayerDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(PlayerDTO.fromEntity(player)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Trouve un joueur par son pseudo. Renvoie le premier joueur trouvé avec ce pseudo.
     * @deprecated Attention en utilisant ce endpoint...
     */
    @Deprecated
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<PlayerDTO> getPlayerByNickname(@PathVariable String nickname) {
        return playerService.getPlayerByNickname(nickname)
                .map(player -> ResponseEntity.ok(PlayerDTO.fromEntity(player)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Trouve tous les joueurs avec un pseudo donné. (préféré)
     */
    @GetMapping("/nicknames/{nickname}")
    public ResponseEntity<List<PlayerDTO>> getAllPlayersByNickname(@PathVariable String nickname) {
        List<PlayerDTO> players = playerService.getAllPlayersByNickname(nickname)
                .stream()
                .map(PlayerDTO::fromEntity)
                .collect(Collectors.toList());
                
        if (players.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(players);
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody Map<String, String> payload) {
        String nickname = payload.get("nickname");
        
        if (nickname == null || nickname.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Player player = playerService.createPlayer(nickname);
        return ResponseEntity.status(HttpStatus.CREATED).body(PlayerDTO.fromEntity(player));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        try {
            player.setPlayerId(id);
            Player updatedPlayer = playerService.updatePlayer(player);
            return ResponseEntity.ok(PlayerDTO.fromEntity(updatedPlayer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/increment-games")
    public ResponseEntity<PlayerDTO> incrementGamesPlayed(@PathVariable Long id) {
        try {
            Player updatedPlayer = playerService.incrementGamesPlayed(id);
            return ResponseEntity.ok(PlayerDTO.fromEntity(updatedPlayer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/increment-victories")
    public ResponseEntity<PlayerDTO> incrementVictories(@PathVariable Long id) {
        try {
            Player updatedPlayer = playerService.incrementVictories(id);
            return ResponseEntity.ok(PlayerDTO.fromEntity(updatedPlayer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/nickname")
    public ResponseEntity<PlayerDTO> updateNickname(
            @PathVariable Long id, 
            @RequestBody PlayerNicknameUpdateRequest request) {
        
        try {
            Player updatedPlayer = playerService.updateNickname(id, request.getNickname());
            return ResponseEntity.ok(PlayerDTO.fromEntity(updatedPlayer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}