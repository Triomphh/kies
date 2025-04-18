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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getGapX() {
        return gapX;
    }

    public void setGapX(Integer gapX) {
        this.gapX = gapX;
    }

    public Integer getGapY() {
        return gapY;
    }

    public void setGapY(Integer gapY) {
        this.gapY = gapY;
    }

    public Account getCreator() {
        return creator;
    }

    public void setCreator(Account creator) {
        this.creator = creator;
    }
}
