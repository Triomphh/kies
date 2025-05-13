package dev.triomph.kies.pojo;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "games")
public class Game {
    
    public enum GameStatus {
        WAITING,
        IN_PROGRESS,
        FINISHED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", referencedColumnName = "player_id", nullable = false)
    private Player creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_id", referencedColumnName = "player_id", nullable = true)
    private Player opponent;

    @Column(name = "max_rounds")
    private Integer maxRounds;

    @Column(name = "turn_limit")
    private Integer turnLimit;

    @Column(name = "creation_timestamp")
    private LocalDateTime creationTimestamp;

    @Column(name = "updated_timestamp")
    private LocalDateTime updatedTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id", referencedColumnName = "player_id")
    private Player winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grid_id", referencedColumnName = "grid_id")
    private Grid grid;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "allow_spectators")
    private Boolean allowSpectators = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GameStatus status = GameStatus.WAITING;
    
    @ElementCollection
    @CollectionTable(name = "game_spectators", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "player_id")
    private List<Long> spectators = new ArrayList<>();

    @Column(name = "creator_secret_character_id")
    private Long creatorSecretCharacterId;

    @Column(name = "opponent_secret_character_id")
    private Long opponentSecretCharacterId;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "creator_grid_state", columnDefinition = "jsonb")
    private String creatorGridState;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opponent_grid_state", columnDefinition = "jsonb")
    private String opponentGridState;

    @Column(name = "current_turn_player_id")
    private Long currentTurnPlayerId;

    @Column(name = "creator_ready")
    private boolean creatorReady = false;

    @Column(name = "opponent_ready")
    private boolean opponentReady = false;

    @Column(name = "current_round", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer currentRound = 1;

    @Column(name = "creator_round_wins", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer creatorRoundWins = 0;

    @Column(name = "opponent_round_wins", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer opponentRoundWins = 0;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Round> rounds = new ArrayList<>();
    

    // Constructeur par défaut
    public Game() {
        this.creationTimestamp = LocalDateTime.now();
        this.updatedTimestamp = LocalDateTime.now();
        this.status = GameStatus.WAITING;
        this.allowSpectators = true;
        this.spectators = new ArrayList<>();
        this.creatorGridState = "{}";
        this.opponentGridState = "{}";
        this.creatorReady = false;
        this.opponentReady = false;
        this.currentRound = 1;
        this.creatorRoundWins = 0;
        this.opponentRoundWins = 0;
        this.rounds = new ArrayList<>();
    }
    
    /**
     * Crée une nouvelle partie avec uniquement les participants.
     *
     * @param creator    Le joueur qui crée la partie
     * @param opponent   Le joueur adverse
     */
    public Game(Player creator, Player opponent) {
        this(); // Appel au constructeur par défaut pour initialiser le timestamp
        this.creator = creator;
        this.opponent = opponent;
    }

    /**
     * Crée une nouvelle partie avec les infos du nombre de tours et de manches.
     *
     * @param creator    Le joueur qui crée la partie
     * @param opponent   Le joueur adverse
     * @param maxRounds  Le nombre maximum de manches
     * @param turnLimit  La limite de tours par manche
     */
    public Game(Player creator, Player opponent, Integer maxRounds, Integer turnLimit) {
        this(creator, opponent);
        this.maxRounds = maxRounds;
        this.turnLimit = turnLimit;
    }
    
    /**
     * Crée un nouveau jeu avec les informations de base et la grille.
     *
     * @param creator    Le joueur qui crée la partie
     * @param opponent   Le joueur adverse
     * @param maxRounds  Le nombre maximum de manches
     * @param turnLimit  La limite de tours par manche
     * @param grid       La grille utilisée pour la partie
     */
    public Game(Player creator, Player opponent, Integer maxRounds, Integer turnLimit, Grid grid) {
        this(creator, opponent, maxRounds, turnLimit);
        this.grid = grid;
    }
    

    // Getters et Setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Player getCreator() {
        return creator;
    }

    public void setCreator(Player creator) {
        this.creator = creator;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
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

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
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
    
    public void addSpectator(Long playerId) {
        if (this.spectators == null) {
            this.spectators = new ArrayList<>();
        }
        this.spectators.add(playerId);
    }
    
    public boolean removeSpectator(Long playerId) {
        if (this.spectators == null) {
            return false;
        }
        return this.spectators.remove(playerId);
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

    public String getCreatorGridState() {
        return creatorGridState;
    }

    public void setCreatorGridState(String creatorGridState) {
        this.creatorGridState = creatorGridState;
    }

    public String getOpponentGridState() {
        return opponentGridState;
    }

    public void setOpponentGridState(String opponentGridState) {
        this.opponentGridState = opponentGridState;
    }

    public Long getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(Long currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
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

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public void addRound(Round round) {
        this.rounds.add(round);
        round.setGame(this);
    }

    public void removeRound(Round round) {
        this.rounds.remove(round);
        round.setGame(null);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTimestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.creationTimestamp = LocalDateTime.now();
        this.updatedTimestamp = LocalDateTime.now();
        if (this.status == null) {
            this.status = GameStatus.WAITING;
        }
        if (this.allowSpectators == null) {
            this.allowSpectators = true;
        }
        if (this.spectators == null) {
            this.spectators = new ArrayList<>();
        }
        if (this.creatorGridState == null || this.creatorGridState.isEmpty()){
            this.creatorGridState = "{}";
        }
        if (this.opponentGridState == null || this.opponentGridState.isEmpty()){
            this.opponentGridState = "{}";
        }
        if (this.currentRound == null) {
            this.currentRound = 1;
        }
        if (this.creatorRoundWins == null) {
            this.creatorRoundWins = 0;
        }
        if (this.opponentRoundWins == null) {
            this.opponentRoundWins = 0;
        }
        if (this.rounds == null) {
            this.rounds = new ArrayList<>();
        }
    }
}
