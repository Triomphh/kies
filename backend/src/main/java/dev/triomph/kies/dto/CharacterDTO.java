package dev.triomph.kies.dto;

import dev.triomph.kies.pojo.Character;

public class CharacterDTO {
    private Long characterId;
    private String name;
    private String imageUrl;
    
    public CharacterDTO() {}
    
    public CharacterDTO(Character character) {
        this.characterId = character.getCharacterId();
        this.name = character.getName();
        this.imageUrl = character.getImageUrl();
    }

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Long characterId) {
        this.characterId = characterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 