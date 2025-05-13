package dev.triomph.kies.dto;

public class GuessDTO {
    private Long gameId;
    private Long guessingPlayerId;
    private Long guessedCharacterId;

    public GuessDTO() {
    }

    public GuessDTO(Long gameId, Long guessingPlayerId, Long guessedCharacterId) {
        this.gameId = gameId;
        this.guessingPlayerId = guessingPlayerId;
        this.guessedCharacterId = guessedCharacterId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGuessingPlayerId() {
        return guessingPlayerId;
    }

    public void setGuessingPlayerId(Long guessingPlayerId) {
        this.guessingPlayerId = guessingPlayerId;
    }

    public Long getGuessedCharacterId() {
        return guessedCharacterId;
    }

    public void setGuessedCharacterId(Long guessedCharacterId) {
        this.guessedCharacterId = guessedCharacterId;
    }
}