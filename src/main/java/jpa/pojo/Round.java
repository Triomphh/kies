package jpa.pojo;

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


    // Getters et Setters
    // ...
}
