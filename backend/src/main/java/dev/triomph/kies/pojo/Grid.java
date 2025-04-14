package dev.triomph.kies.pojo;

import java.util.List;

import jakarta.persistence.*;

// temporaire
class Character {}


@Entity
@Table(name = "grids")
public class Grid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grid_id")
    private Long gridId;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rows")
    private Integer rows;

    @Column(name = "columns")
    private Integer columns;

    @Transient
    private List<Character> characters;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

    @Column(name = "gap_x")
    private Integer gapX;

    @Column(name = "gap_y")
    private Integer gapY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "account_id")
    private Account creator;
    
    
    // Getters et Setters
    // ...
    
}
