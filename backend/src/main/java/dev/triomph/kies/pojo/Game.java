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
    
    
    // Getters et Setters
    // ...

}
