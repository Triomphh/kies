package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.service.GameService;
import dev.triomph.kies.service.GridService;
import dev.triomph.kies.service.PlayerService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final GridService gridService;

    public GameController(GameService gameService, PlayerService playerService, GridService gridService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.gridService = gridService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Map<String, Object> payload) {
        try {
            Long creatorId = Long.parseLong(payload.get("creatorId").toString());
            Long opponentId = Long.parseLong(payload.get("opponentId").toString());
            Integer maxRounds = (Integer) payload.get("maxRounds");
            Integer turnLimit = (Integer) payload.get("turnLimit");
            Long gridId = Long.parseLong(payload.get("gridId").toString());

            Optional<Player> creatorOpt = playerService.getPlayerById(creatorId);
            Optional<Player> opponentOpt = playerService.getPlayerById(opponentId);
            Optional<Grid> gridOpt = gridService.getGridById(gridId);

            if (creatorOpt.isPresent() && opponentOpt.isPresent() && gridOpt.isPresent()) {
                Game game = gameService.createGame(
                    creatorOpt.get(), 
                    opponentOpt.get(), 
                    maxRounds, 
                    turnLimit, 
                    gridOpt.get()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(game);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game game) {
        if (!gameService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        game.setGameId(id);
        return ResponseEntity.ok(gameService.updateGame(game));
    }

    @PutMapping("/{id}/winner/{playerId}")
    public ResponseEntity<Game> setGameWinner(@PathVariable Long id, @PathVariable Long playerId) {
        Optional<Player> winnerOpt = playerService.getPlayerById(playerId);
        if (!gameService.getGameById(id).isPresent() || !winnerOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Game updatedGame = gameService.setWinner(id, winnerOpt.get());
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        if (!gameService.getGameById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}