package dev.triomph.kies.dto;

import java.util.ArrayList;
import java.util.List;

import dev.triomph.kies.pojo.Grid;

public class GridDTO {
    private Long gridId;
    private String name;
    private boolean isOfficial;
    private String creator;
    private Long creatorId;
    private List<CharacterDTO> characters = new ArrayList<>();
    
    public GridDTO() {}
    
    public GridDTO(Grid grid) {
        this.gridId = grid.getGridId();
        this.name = grid.getName();
        this.isOfficial = grid.isOfficial();
        
        if (grid.getCreator() != null) {
            this.creatorId = grid.getCreator().getAccountId();
            if (grid.getCreator().getPlayer() != null) {
                this.creator = grid.getCreator().getPlayer().getNickname();
            }
        }
        
        if (grid.getCharacters() != null) {
            grid.getCharacters().forEach(character -> 
                this.characters.add(new CharacterDTO(character))
            );
        }
    }

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
        return isOfficial;
    }

    public void setOfficial(boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<CharacterDTO> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CharacterDTO> characters) {
        this.characters = characters;
    }
} 