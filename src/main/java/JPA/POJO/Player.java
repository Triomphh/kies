package JPA.POJO;

import jakarta.persistence.*;

import java.io.Serializable;


@Entity
@Table(name = "players")
public class Player implements Serializable {
    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int gamesPlayed;
    private int victories;


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

    public int getVictories() {
        return victories;
    }
    public void setVictories(int victories) {
        this.victories = victories;
    }

}

