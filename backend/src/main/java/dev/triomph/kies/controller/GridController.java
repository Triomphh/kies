package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.service.AccountService;
import dev.triomph.kies.service.CategoryService;
import dev.triomph.kies.service.GridService;

@RestController
@RequestMapping("/api/grids")
public class GridController {

    private final GridService gridService;
    private final CategoryService categoryService;
    private final AccountService accountService;

    public GridController(GridService gridService, CategoryService categoryService, AccountService accountService) {
        this.gridService = gridService;
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Grid>> getAllGrids() {
        return ResponseEntity.ok(gridService.getAllGrids());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grid> getGridById(@PathVariable Long id) {
        return gridService.getGridById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Grid> createGrid(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            Long categoryId = Long.parseLong(payload.get("categoryId").toString());
            Long creatorId = Long.parseLong(payload.get("creatorId").toString());

            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            Optional<Account> creatorOpt = accountService.getAccountById(creatorId);
            
            if (categoryOpt.isPresent() && creatorOpt.isPresent()) {
                Grid grid = gridService.createGrid(
                    name,
                    categoryOpt.get(),
                    creatorOpt.get()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(grid);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @PostMapping("/{id}/characters")
    public ResponseEntity<Grid> addCharacterToGrid(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String imageUrl = (String) payload.get("imageUrl");
            Long creatorId = Long.parseLong(payload.get("creatorId").toString());

            Optional<Account> creatorOpt = accountService.getAccountById(creatorId);
            
            if (creatorOpt.isPresent()) {
                Grid updatedGrid = gridService.addCharacterToGrid(id, name, imageUrl, creatorOpt.get());
                return ResponseEntity.ok(updatedGrid);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    @DeleteMapping("/{gridId}/characters/{characterId}")
    public ResponseEntity<Grid> removeCharacterFromGrid(@PathVariable Long gridId, @PathVariable Long characterId) {
        try {
            Grid updatedGrid = gridService.removeCharacterFromGrid(gridId, characterId);
            return ResponseEntity.ok(updatedGrid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grid> updateGrid(@PathVariable Long id, @RequestBody Grid grid) {
        if (!gridService.getGridById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        grid.setGridId(id);
        return ResponseEntity.ok(gridService.updateGrid(grid));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrid(@PathVariable Long id) {
        if (!gridService.getGridById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        gridService.deleteGrid(id);
        return ResponseEntity.noContent().build();
    }
}