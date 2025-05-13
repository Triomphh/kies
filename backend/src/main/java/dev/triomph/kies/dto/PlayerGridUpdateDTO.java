package dev.triomph.kies.dto;

public class PlayerGridUpdateDTO {
    private Long gameId;
    private Long playerId;
    private Long characterId;
    private boolean isFlippedDown;

    public PlayerGridUpdateDTO() {
    }

    public PlayerGridUpdateDTO(Long gameId, Long playerId, Long characterId, boolean isFlippedDown) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.characterId = characterId;
        this.isFlippedDown = isFlippedDown;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public boolean getIsFlippedDown() {
        return isFlippedDown;
    }

    public void setIsFlippedDown(boolean isFlippedDown) {
        this.isFlippedDown = isFlippedDown;
    }
}