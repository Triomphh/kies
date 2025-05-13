package dev.triomph.kies.dto;

public class StartGameRequestDTO {
    private Long creatorPlayerId;
    private Long creatorSecretCharacterId;
    private Long opponentPlayerId;
    private Long opponentSecretCharacterId;

    public StartGameRequestDTO() {
    }

    public StartGameRequestDTO(Long creatorPlayerId, Long creatorSecretCharacterId, Long opponentPlayerId, Long opponentSecretCharacterId) {
        this.creatorPlayerId = creatorPlayerId;
        this.creatorSecretCharacterId = creatorSecretCharacterId;
        this.opponentPlayerId = opponentPlayerId;
        this.opponentSecretCharacterId = opponentSecretCharacterId;
    }

    public Long getCreatorPlayerId() {
        return creatorPlayerId;
    }

    public void setCreatorPlayerId(Long creatorPlayerId) {
        this.creatorPlayerId = creatorPlayerId;
    }

    public Long getCreatorSecretCharacterId() {
        return creatorSecretCharacterId;
    }

    public void setCreatorSecretCharacterId(Long creatorSecretCharacterId) {
        this.creatorSecretCharacterId = creatorSecretCharacterId;
    }

    public Long getOpponentPlayerId() {
        return opponentPlayerId;
    }

    public void setOpponentPlayerId(Long opponentPlayerId) {
        this.opponentPlayerId = opponentPlayerId;
    }

    public Long getOpponentSecretCharacterId() {
        return opponentSecretCharacterId;
    }

    public void setOpponentSecretCharacterId(Long opponentSecretCharacterId) {
        this.opponentSecretCharacterId = opponentSecretCharacterId;
    }
}