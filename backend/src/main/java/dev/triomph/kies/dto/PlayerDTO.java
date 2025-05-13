package dev.triomph.kies.dto;

import dev.triomph.kies.pojo.Player;

public class PlayerDTO {
    private Long playerId;
    private String nickname;
    private int gamesPlayed;
    private int victories;
    private String profileImageUrl;
    
    public PlayerDTO() {
    }
    
    public PlayerDTO(Long playerId, String nickname, int gamesPlayed, int victories) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.gamesPlayed = gamesPlayed;
        this.victories = victories;
    }
    
    public PlayerDTO(Player player) {
        this.playerId = player.getPlayerId();
        this.nickname = player.getNickname();
        this.gamesPlayed = player.getGamesPlayed();
        this.victories = player.getVictories();
        
        this.profileImageUrl = player.getProfileImageUrl();
        if (this.profileImageUrl == null || this.profileImageUrl.isEmpty()) {
            this.profileImageUrl = "https://api.dicebear.com/6.x/personas/png?seed=" + 
                    player.getNickname().toLowerCase().replaceAll("[^a-z0-9]", "") + 
                    "&backgroundColor=b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf&size=128";
        }
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
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
} 