package dev.triomph.kies.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Entity
@Table(name = "grids")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "gridId")
public class Grid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grid_id")
    private Long gridId;

    @Column(name = "name")
    private String name;

    @Column(name = "is_official")
    private Boolean isOfficial = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "grid", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Character> characters = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "account_id")
    private Account creator;

    // Constructeurs
    public Grid() {}

    /**
     * Crée une nouvelle grille avec les informations essentielles.
     *
     * @param name     Le nom de la grille
     * @param category La catégorie de la grille
     */
    public Grid(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    /**
     * Crée une nouvelle grille avec les informations complètes.
     *
     * @param name       Le nom de la grille
     * @param category   La catégorie de la grille
     * @param creator    Le compte qui a créé cette grille
     * @param isOfficial Indique si la grille est officielle
     */
    public Grid(String name, Category category, Account creator, boolean isOfficial) {
        this(name, category);
        this.creator = creator;
        this.isOfficial = isOfficial;
    }

    /**
     * Crée une nouvelle grille avec les informations complètes (non-officielle par défaut).
     *
     * @param name       Le nom de la grille
     * @param category   La catégorie de la grille
     * @param creator    Le compte qui a créé cette grille
     */
    public Grid(String name, Category category, Account creator) {
        this(name, category, creator, false);
    }

    public void addCharacter(Character character) {
        // Check si on a atteint la limite de 32 personnages
        if (characters.size() >= 32) {
            throw new IllegalStateException("Grid cannot have more than 32 characters");
        }
        
        characters.add(character);
        character.setGrid(this);
    }

    public void removeCharacter(Character character) {
        characters.remove(character);
        character.setGrid(null);
    }

    // Getters et Setters
    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOfficial() {
        return isOfficial != null ? isOfficial : false;
    }

    public void setOfficial(Boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters.clear();
        if (characters != null) {
            for (Character character : characters) {
                addCharacter(character);
            }
        }
    }

    public Account getCreator() {
        return creator;
    }

    public void setCreator(Account creator) {
        this.creator = creator;
    }
}
