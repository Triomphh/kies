package dev.triomph.kies.dto;

public class JoinGameRequest {
    private Long playerId;
    private String password;
    private Boolean asSpectator;
    
    public JoinGameRequest() {
    }
    
    public Long getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Boolean getAsSpectator() {
        return asSpectator;
    }
    
    public void setAsSpectator(Boolean asSpectator) {
        this.asSpectator = asSpectator;
    }
}
