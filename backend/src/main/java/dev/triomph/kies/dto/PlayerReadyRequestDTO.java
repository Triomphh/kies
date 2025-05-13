package dev.triomph.kies.dto;

public class PlayerReadyRequestDTO {
    private boolean isReady;

    public PlayerReadyRequestDTO() {}

    public PlayerReadyRequestDTO(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    @Override
    public String toString() {
        return "PlayerReadyRequestDTO{" +
               "isReady=" + isReady +
               '}';
    }
}