package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.CharacterDAO;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Character;
import dev.triomph.kies.pojo.Grid;

@Service
public class CharacterService {

    private final CharacterDAO characterDAO;

    public CharacterService(CharacterDAO characterDAO) {
        this.characterDAO = characterDAO;
    }

    public List<Character> getAllCharacters() {
        return characterDAO.findAll();
    }

    public Optional<Character> getCharacterById(Long id) {
        return characterDAO.findById(id);
    }

    public Character createCharacter(String name, String imageUrl, Account creator) {
        Character character = new Character(name, imageUrl, creator);
        return characterDAO.save(character);
    }

    public Character createCharacter(String name, String imageUrl, Grid grid, Account creator) {
        Character character = new Character(name, imageUrl, grid, creator);
        return characterDAO.save(character);
    }

    public Character updateCharacter(Character character) {
        return characterDAO.save(character);
    }

    public void deleteCharacter(Long id) {
        characterDAO.deleteById(id);
    }
}