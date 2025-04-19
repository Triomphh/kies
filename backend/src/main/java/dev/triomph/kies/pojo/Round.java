package dev.triomph.kies.pojo;

import jakarta.persistence.*;


@Entity
@Table(name = "rounds")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long roundId;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner", referencedColumnName = "player_id")
    private Player winner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game", referencedColumnName = "game_id")
    private Game game;


    // Constructeur par défaut
    public Round() {
        this.status = Status.IN_PROGRESS;
    }
    
    /**
     * Crée un nouveau round
     *
     * @param roundNumber  Le numéro du round dans la partie
     * @param game         La partie
     */
    public Round(Integer roundNumber, Game game) {
        this();
        this.roundNumber = roundNumber;
        this.game = game;
    }
    
    /**
     * Crée un nouveau round avec son statut.
     *
     * @param roundNumber  Le numéro du round dans la partie
     * @param game         La partie
     * @param status       Le statut du round 
     */
    public Round(Integer roundNumber, Game game, Status status) {
        this(roundNumber, game);
        this.status = status;
    }


    // Getters et Setters
    public Long getRoundId() {
        return roundId;
    }

    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
