package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.GameDAO;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Player;

@Service
public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<Game> getAllGames() {
        return gameDAO.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameDAO.findById(id);
    }

    public Game createGame(Player creator, Player opponent, Integer maxRounds, Integer turnLimit, Grid grid) {
        Game game = new Game(creator, opponent, maxRounds, turnLimit, grid);
        return gameDAO.save(game);
    }

    public Game updateGame(Game game) {
        return gameDAO.save(game);
    }

    public Game setWinner(Long gameId, Player winner) {
        Optional<Game> gameOpt = gameDAO.findById(gameId);
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            game.setWinner(winner);
            return gameDAO.save(game);
        }
        return null;
    }

    public void deleteGame(Long id) {
        gameDAO.deleteById(id);
    }
}