package dev.triomph.kies.pojo;

import jakarta.persistence.*;


@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "account_id")
    private Account creator;
    

    /**
     * Crée une nouvelle catégorie avec un nom.
     *
     * @param name  Le nom de la catégorie
     */
    public Category(String name) {
        this.name = name;
    }
    
    /**
     * Crée une nouvelle catégorie avec un nom et son créateur.
     *
     * @param name     Le nom de la catégorie
     * @param creator  Le compte (Account) qui a créé cette catégorie
     */
    public Category(String name, Account creator) {
        this(name);
        this.creator = creator;
    }
    

    // Getters et Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getCreator() {
        return creator;
    }

    public void setCreator(Account creator) {
        this.creator = creator;
    }
}
