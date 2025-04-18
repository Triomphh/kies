package dev.triomph.kies.pojo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDateTime dateRegistered;

    // Relation 1:1 obligatoire avec Player
    @OneToOne(fetch = FetchType.LAZY, optional = false) // Un compte doit obligatoirement appartenir à un joueur
    @JoinColumn(name = "player_id", nullable = false, unique = true) // Colonne de clé étrangère
    @JsonBackReference
    private Player player;


    // Constructeur par défaut
    public Account() {
        this.dateRegistered = LocalDateTime.now();
    }

    /**
     * Crée un nouveau compte à un Player.
     *
     * @param player    Le Player auquel appartient ce compte.
     * @param password  mot de passe de l'utilisateur.
     * @param age       âge de l'utilisateur.
     * @param gender    genre de l'utilisateur.
     */
    public Account(Player player, String password, int age, Gender gender) {
        this();
        this.player = player;
        this.password = password;
        this.age = age;
        this.gender = gender;
    }


    // Getters et Setters

    public Long getId() {
        return accountId;
    }
    public void setId(Long accountId) {
        this.accountId = accountId;
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

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }
    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
}