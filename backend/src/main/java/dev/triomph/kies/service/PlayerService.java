package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.triomph.kies.DAO.PlayerDAO;
import dev.triomph.kies.pojo.Player;


@Service
public class PlayerService {

    private final PlayerDAO playerDAO;

    public PlayerService(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    public List<Player> getAllPlayers() {
        return playerDAO.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return playerDAO.findById(id);
    }

    /**
     * Obtient le premier joueur avec le pseudo donné
     * @deprecated Attention en utilisant la méthode...
     */
    public Optional<Player> getPlayerByNickname(String nickname) {
        return playerDAO.findByNickname(nickname);
    }
    
    public List<Player> getAllPlayersByNickname(String nickname) {
        return playerDAO.findAllByNickname(nickname);
    }

    public Player createPlayer(String nickname) {
        Player player = new Player(nickname);
        return playerDAO.save(player);
    }

    public Player updatePlayer(Player player) {
        if (!playerDAO.existsById(player.getPlayerId())) {
            throw new IllegalArgumentException("Player not found with id: " + player.getPlayerId());
        }
        return playerDAO.save(player);
    }
    
    @Transactional
    public Player updateNickname(Long playerId, String newNickname) {
        Player player = playerDAO.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        
        player.setNickname(newNickname);
        return playerDAO.save(player);
    }

    public void deletePlayer(Long id) {
        if (!playerDAO.existsById(id)) {
            throw new IllegalArgumentException("Player not found with id: " + id);
        }
        playerDAO.deleteById(id);
    }

    @Transactional
    public Player incrementGamesPlayed(Long playerId) {
        Player player = playerDAO.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        
        player.setGamesPlayed(player.getGamesPlayed() + 1);
        return playerDAO.save(player);
    }

    @Transactional
    public Player incrementVictories(Long playerId) {
        Player player = playerDAO.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        
        player.setVictories(player.getVictories() + 1);
        return playerDAO.save(player);
    }
}