package JPA.POJO;

import jakarta.persistence.*;


import java.io.Serializable;

enum sexe{
    HOMME,
    FEMME,
    AUTRE
}

@Entity
@Table(name = "Player")
public class Player implements Serializable {
    @Id
    private String pseudo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "age", nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    private sexe gender;

}

