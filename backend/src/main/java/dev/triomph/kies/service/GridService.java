package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.GridDAO;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Grid;
import jakarta.persistence.EntityNotFoundException;

@Service
public class GridService {

    private final GridDAO gridDAO;
    private final CharacterService characterService;

    public GridService(GridDAO gridDAO, CharacterService characterService) {
        this.gridDAO = gridDAO;
        this.characterService = characterService;
    }

    public List<Grid> getAllGrids() {
        return gridDAO.findAll();
    }

    public Optional<Grid> getGridById(Long id) {
        return gridDAO.findById(id);
    }

    public Grid createGrid(String name, Category category, Account creator) {
        Grid grid = new Grid(name, category, creator);
        return gridDAO.save(grid);
    }

    public Grid updateGrid(Grid grid) {
        return gridDAO.save(grid);
    }

    public Grid addCharacterToGrid(Long gridId, String characterName, String imageUrl, Account creator) {
        Grid grid = gridDAO.findById(gridId)
                .orElseThrow(() -> new EntityNotFoundException("Grid not found with id: " + gridId));
        
        if (grid.getCharacters().size() >= 32) {
            throw new IllegalStateException("Grid cannot have more than 32 characters");
        }
        
        characterService.createCharacter(characterName, imageUrl, grid, creator);
        
        return gridDAO.findById(gridId).get();
    }

    public Grid removeCharacterFromGrid(Long gridId, Long characterId) {
        Grid grid = gridDAO.findById(gridId)
                .orElseThrow(() -> new EntityNotFoundException("Grid not found with id: " + gridId));
        
        grid.getCharacters().stream()
            .filter(c -> c.getCharacterId().equals(characterId))
            .findFirst()
            .ifPresent(grid::removeCharacter);
            
        return gridDAO.save(grid);
    }

    public void deleteGrid(Long id) {
        gridDAO.deleteById(id);
    }
}