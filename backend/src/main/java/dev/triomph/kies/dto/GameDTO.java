package dev.triomph.kies.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.triomph.kies.pojo.Game;
import dev.triomph.kies.pojo.Game.GameStatus;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Player;

public class GameDTO {
    private Long gameId;
    private PlayerDTO creator;
    private PlayerDTO opponent;
    private Integer maxRounds;
    private Integer turnLimit;
    private LocalDateTime creationTimestamp;
    private LocalDateTime updatedTimestamp;
    private PlayerDTO winner;
    private GridDTO gridInfo;
    private Boolean allowSpectators;
    private GameStatus status;
    private List<Long> spectators;
    private Boolean hasPassword;

    private Long creatorSecretCharacterId;
    private Long opponentSecretCharacterId;
    private Map<Long, Boolean> creatorGridState;
    private Map<Long, Boolean> opponentGridState;
    private Long currentTurnPlayerId;
    private String currentTurnPlayerNickname;

    private boolean creatorReady;
    private boolean opponentReady;

    private Integer currentRound;
    private Integer creatorRoundWins;
    private Integer opponentRoundWins;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public GameDTO() {
    }
    
    public GameDTO(Game game, boolean summary) {
        this.gameId = game.getGameId();
        if (game.getCreator() != null) {
            this.creator = new PlayerDTO(game.getCreator());
        }
        if (game.getOpponent() != null) {
            this.opponent = new PlayerDTO(game.getOpponent());
        }
        this.status = game.getStatus();
        this.creationTimestamp = game.getCreationTimestamp();
        this.updatedTimestamp = game.getUpdatedTimestamp();
        this.allowSpectators = game.getAllowSpectators();
        this.hasPassword = (game.getPassword() != null && !game.getPassword().isEmpty());
        if (game.getGrid() != null) {
            this.gridInfo = new GridDTO(game.getGrid());
        }
        this.creatorReady = game.isCreatorReady();
        this.opponentReady = game.isOpponentReady();
        this.currentRound = game.getCurrentRound();
        this.creatorRoundWins = game.getCreatorRoundWins();
        this.opponentRoundWins = game.getOpponentRoundWins();
    }


    public GameDTO(Game game, Long requestingPlayerId) {
        this.gameId = game.getGameId();
        
        Player gameCreator = game.getCreator();
        if (gameCreator != null) {
            this.creator = new PlayerDTO(gameCreator);
        }
        
        Player gameOpponent = game.getOpponent();
        if (gameOpponent != null) {
            this.opponent = new PlayerDTO(gameOpponent);
        }
        
        this.maxRounds = game.getMaxRounds();
        this.turnLimit = game.getTurnLimit();
        this.creationTimestamp = game.getCreationTimestamp();
        this.updatedTimestamp = game.getUpdatedTimestamp();
        
        Player gameWinner = game.getWinner();
        if (gameWinner != null) {
            this.winner = new PlayerDTO(gameWinner);
        }
        
        Grid gameGrid = game.getGrid();
        if (gameGrid != null) {
            this.gridInfo = new GridDTO(gameGrid);
        }
        
        this.allowSpectators = game.getAllowSpectators();
        this.status = game.getStatus();
        this.spectators = game.getSpectators();
        this.hasPassword = (game.getPassword() != null && !game.getPassword().isEmpty());
        this.currentTurnPlayerId = game.getCurrentTurnPlayerId();

        if (this.currentTurnPlayerId != null) {
            if (game.getCreator() != null && this.currentTurnPlayerId.equals(game.getCreator().getPlayerId())) {
                this.currentTurnPlayerNickname = game.getCreator().getNickname();
            } else if (game.getOpponent() != null && this.currentTurnPlayerId.equals(game.getOpponent().getPlayerId())) {
                this.currentTurnPlayerNickname = game.getOpponent().getNickname();
            }
        }

        this.creatorReady = game.isCreatorReady();
        this.opponentReady = game.isOpponentReady();

        this.currentRound = game.getCurrentRound();
        this.creatorRoundWins = game.getCreatorRoundWins();
        this.opponentRoundWins = game.getOpponentRoundWins();
 
        try {
            if (requestingPlayerId != null && gameCreator != null && requestingPlayerId.equals(gameCreator.getPlayerId())) {
                if (game.getCreatorGridState() != null) {
                    this.creatorGridState = objectMapper.readValue(game.getCreatorGridState(), new TypeReference<Map<Long, Boolean>>() {});
                }
            }

            if (requestingPlayerId != null && gameOpponent != null && requestingPlayerId.equals(gameOpponent.getPlayerId())) {
                if (game.getOpponentGridState() != null) {
                    this.opponentGridState = objectMapper.readValue(game.getOpponentGridState(), new TypeReference<Map<Long, Boolean>>() {});
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing grid states for game " + game.getGameId() + ": " + e.getMessage());
        }

        if (requestingPlayerId != null) {
            if (gameCreator != null && requestingPlayerId.equals(gameCreator.getPlayerId())) {
                this.creatorSecretCharacterId = game.getCreatorSecretCharacterId();
            }
            if (gameOpponent != null && requestingPlayerId.equals(gameOpponent.getPlayerId())) {
                this.opponentSecretCharacterId = game.getOpponentSecretCharacterId();
            }
            if (game.getStatus() == GameStatus.FINISHED) {
                 this.creatorSecretCharacterId = game.getCreatorSecretCharacterId();
                 this.opponentSecretCharacterId = game.getOpponentSecretCharacterId();
            }
        } else {
            if (game.getStatus() == GameStatus.FINISHED) {
                 this.creatorSecretCharacterId = game.getCreatorSecretCharacterId();
                 this.opponentSecretCharacterId = game.getOpponentSecretCharacterId();
            }
        }
    }


    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public PlayerDTO getCreator() {
        return creator;
    }

    public void setCreator(PlayerDTO creator) {
        this.creator = creator;
    }

    public PlayerDTO getOpponent() {
        return opponent;
    }

    public void setOpponent(PlayerDTO opponent) {
        this.opponent = opponent;
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

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public PlayerDTO getWinner() {
        return winner;
    }

    public void setWinner(PlayerDTO winner) {
        this.winner = winner;
    }

    public GridDTO getGridInfo() {
        return gridInfo;
    }

    public void setGridInfo(GridDTO gridInfo) {
        this.gridInfo = gridInfo;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public Long getCreatorSecretCharacterId() {
        return creatorSecretCharacterId;
    }

    public void setCreatorSecretCharacterId(Long creatorSecretCharacterId) {
        this.creatorSecretCharacterId = creatorSecretCharacterId;
    }

    public Long getOpponentSecretCharacterId() {
        return opponentSecretCharacterId;
    }

    public void setOpponentSecretCharacterId(Long opponentSecretCharacterId) {
        this.opponentSecretCharacterId = opponentSecretCharacterId;
    }

    public Map<Long, Boolean> getCreatorGridState() {
        return creatorGridState;
    }

    public void setCreatorGridState(Map<Long, Boolean> creatorGridState) {
        this.creatorGridState = creatorGridState;
    }

    public Map<Long, Boolean> getOpponentGridState() {
        return opponentGridState;
    }

    public void setOpponentGridState(Map<Long, Boolean> opponentGridState) {
        this.opponentGridState = opponentGridState;
    }

    public Long getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public Boolean getAllowSpectators() {
        return allowSpectators;
    }

    public void setAllowSpectators(Boolean allowSpectators) {
        this.allowSpectators = allowSpectators;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<Long> getSpectators() {
        return spectators;
    }

    public void setSpectators(List<Long> spectators) {
        this.spectators = spectators;
    }

    public Boolean getHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(Boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public boolean isCreatorReady() {
        return creatorReady;
    }

    public void setCreatorReady(boolean creatorReady) {
        this.creatorReady = creatorReady;
    }

    public boolean isOpponentReady() {
        return opponentReady;
    }

    public void setOpponentReady(boolean opponentReady) {
        this.opponentReady = opponentReady;
    }

    // Getters and Setters for new round fields
    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public Integer getCreatorRoundWins() {
        return creatorRoundWins;
    }

    public void setCreatorRoundWins(Integer creatorRoundWins) {
        this.creatorRoundWins = creatorRoundWins;
    }

    public Integer getOpponentRoundWins() {
        return opponentRoundWins;
    }

    public void setOpponentRoundWins(Integer opponentRoundWins) {
        this.opponentRoundWins = opponentRoundWins;
    }

    public String getCurrentTurnPlayerNickname() {
        return currentTurnPlayerNickname;
    }

    public void setCurrentTurnPlayerNickname(String currentTurnPlayerNickname) {
        this.currentTurnPlayerNickname = currentTurnPlayerNickname;
    }
}
