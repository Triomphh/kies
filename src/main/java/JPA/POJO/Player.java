package JPA.POJO;

import jakarta.persistence.*;


import java.io.Serializable;

enum Genre{
    HOMME,
    FEMME,
    AUTRE
}

@Entity
@Table(name = "Player")
public class Player implements Serializable {
    @Id
    private String pseudo;

    @Column(name = "motDePasse", nullable = false)
    private String motDePasse;

    @Column(name = "age", nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    // Nombre de parties jouées
    @Column(name = "nb_parties", nullable = false)
    private int nb_parties = 0;

    // Nombre de parties gagnées
    @Column(name = "nb_victoires", nullable = false)
    private int nb_victoires = 0;

    // Nombre de parties perdues
    @Column(name = "nb_defaites", nullable = false)
    private int nb_defaites = 0;

    // Nombre moyen de parties perdues/gagnées
    @Column(name = "nb_moyen_dv")
    private float nb_moyen_dv;

    @Column(name = "score_moyen")
    private float score_moyen;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPassword() {
        return motDePasse;
    }

    public void setPassword(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getNb_parties() {
        return nb_parties;
    }

    public void setNb_parties(int nb_parties) {
        this.nb_parties = nb_parties;
    }

    public float getNb_moyen_dv() {
        return nb_moyen_dv;
    }

    public void setNb_moyen_dv(float nb_moyen_dv) {
        this.nb_moyen_dv = nb_moyen_dv;
    }

    public int getNb_victoires() {
        return nb_victoires;
    }

    public void setNb_victoires(int nb_victoires) {
        this.nb_victoires = nb_victoires;
    }

    public int getNb_defaites() {
        return nb_defaites;
    }

    public void setNb_defaites(int nb_defaites) {
        this.nb_defaites = nb_defaites;
    }
}

