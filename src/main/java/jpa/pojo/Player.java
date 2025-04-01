package jpa.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    private int gamesPlayed;
    private int victories;


    // Relation 1:1 optionnelle avec Account
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
        // À implémenter
    }

}

