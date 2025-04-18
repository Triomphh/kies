package dev.triomph.kies.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long playerId;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    private int gamesPlayed;
    private int victories;


    // Relation 1:1 optionnelle avec Account
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    @JsonManagedReference
    private Account account;


    // Constructeur par défaut
    public Player() {
        this.gamesPlayed = 0;
        this.victories = 0;
    }

    /**
     * Crée un nouveau Player
     *
     * @param nickname pseudo du joueur
     */
    public Player(String nickname) {
        this();
        this.nickname = nickname;
    }


    // Getters et Setters

    public Long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getVictories() {
        return victories;
    }
    public void setVictories(int victories) {
        this.victories = victories;
    }

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

}

