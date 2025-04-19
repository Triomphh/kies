package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Round;
import dev.triomph.kies.pojo.Status;
import dev.triomph.kies.service.GameService;
import dev.triomph.kies.service.PlayerService;
import dev.triomph.kies.service.RoundService;

@RestController
@RequestMapping("/api/rounds")
public class RoundController {

    private final RoundService roundService;
    private final GameService gameService;
    private final PlayerService playerService;

    public RoundController(RoundService roundService, GameService gameService, PlayerService playerService) {
        this.roundService = roundService;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Round>> getAllRounds() {
        return ResponseEntity.ok(roundService.getAllRounds());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Round> getRoundById(@PathVariable Long id) {
        return roundService.getRoundById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Round> createRound(@RequestBody Map<String, Object> payload) {
        try {
            Integer roundNumber = (Integer) payload.get("roundNumber");
            Long gameId = Long.parseLong(payload.get("gameId").toString());

            Optional<Game> gameOpt = gameService.getGameById(gameId);

            if (gameOpt.isPresent()) {
                Round round = roundService.createRound(roundNumber, gameOpt.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(round);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Round> updateRound(@PathVariable Long id, @RequestBody Round round) {
        if (!roundService.getRoundById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        round.setRoundId(id);
        return ResponseEntity.ok(roundService.updateRound(round));
    }

    @PutMapping("/{id}/winner/{playerId}")
    public ResponseEntity<Round> setRoundWinner(@PathVariable Long id, @PathVariable Long playerId) {
        Optional<Player> winnerOpt = playerService.getPlayerById(playerId);
        if (!roundService.getRoundById(id).isPresent() || !winnerOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Round updatedRound = roundService.setWinner(id, winnerOpt.get());
        return ResponseEntity.ok(updatedRound);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Round> updateRoundStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            Round updatedRound = roundService.updateStatus(id, newStatus);
            
            if (updatedRound != null) {
                return ResponseEntity.ok(updatedRound);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRound(@PathVariable Long id) {
        if (!roundService.getRoundById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        roundService.deleteRound(id);
        return ResponseEntity.noContent().build();
    }
}