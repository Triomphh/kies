package dev.triomph.kies.pojo;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator", referencedColumnName = "player_id", nullable = false)
    private Player creator;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opponent", referencedColumnName = "player_id", nullable = false)
    private Player opponent;

    @Column(name = "max_rounds")
    private Integer maxRounds;

    @Column(name = "turn_limit")
    private Integer turnLimit;

    @Column(name = "creation_timestamp")
    private LocalDateTime creationTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner", referencedColumnName = "player_id")
    private Player winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grid_id", referencedColumnName = "grid_id")
    private Grid grid;
    

    // Constructeur par défaut
    public Game() {
        this.creationTimestamp = LocalDateTime.now();
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
}
