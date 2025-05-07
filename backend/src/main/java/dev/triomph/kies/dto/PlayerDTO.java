package dev.triomph.kies.dto;

import dev.triomph.kies.pojo.Player;

public class PlayerDTO {
    private Long playerId;
    private String nickname;
    private int gamesPlayed;
    private int victories;
    
    public PlayerDTO() {
    }
    
    public PlayerDTO(Long playerId, String nickname, int gamesPlayed, int victories) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.gamesPlayed = gamesPlayed;
        this.victories = victories;
    }
    
    public static PlayerDTO fromEntity(Player player) {
        return new PlayerDTO(
            player.getPlayerId(),
            player.getNickname(),
            player.getGamesPlayed(),
            player.getVictories()
        );
    }
    
    public Long getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
    
    public int getVictories() {
        return victories;
    }
    
    public void setVictories(int victories) {
        this.victories = victories;
    }
} 