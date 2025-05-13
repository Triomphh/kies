package dev.triomph.kies.dto;

public class CreateGameRequestDTO {
    private Long creatorPlayerId;
    private Integer maxRounds;
    private Integer turnLimit;
    private Long gridId;
    private String password;
    private Boolean allowSpectators;

    public CreateGameRequestDTO() {
    }

    public CreateGameRequestDTO(Long creatorPlayerId, Integer maxRounds, Integer turnLimit, Long gridId, String password, Boolean allowSpectators) {
        this.creatorPlayerId = creatorPlayerId;
        this.maxRounds = maxRounds;
        this.turnLimit = turnLimit;
        this.gridId = gridId;
        this.password = password;
        this.allowSpectators = allowSpectators;
    }

    public Long getCreatorPlayerId() {
        return creatorPlayerId;
    }

    public void setCreatorPlayerId(Long creatorPlayerId) {
        this.creatorPlayerId = creatorPlayerId;
    }

    public Integer getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(Integer maxRounds) {
        this.maxRounds = maxRounds;
    }

    public Integer getTurnLimit() {
        return turnLimit;
    }

    public void setTurnLimit(Integer turnLimit) {
        this.turnLimit = turnLimit;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAllowSpectators() {
        return allowSpectators;
    }

    public void setAllowSpectators(Boolean allowSpectators) {
        this.allowSpectators = allowSpectators;
    }
}