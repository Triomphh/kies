package jpa.pojo;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDateTime dateRegistered;

    private int gamesPlayed;
    private int victories;


    // Constructeur par défaut
    public Player() {
        this.dateRegistered = LocalDateTime.now();
        this.gamesPlayed = 0;
        this.victories = 0;
    }

    /**
     * Crée un nouveau Player 
     * 
     * @param username   pseudo du joueur
     * @param password   mot de passe du joueur
     * @param age        âge du joueur
     * @param gender     genre du joueur
     */
    public Player(String username, String password, int age, Gender gender) {
        this();
        this.username = username;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }


    // Getters et setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }
    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public int getVictories() {
        return victories;
    }
    public void setVictories(int victories) {
        this.victories = victories;
    }

}

