package dev.triomph.kies.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import dev.triomph.kies.service.GridService;
import dev.triomph.kies.service.CategoryService;
import dev.triomph.kies.service.AccountService;
import dev.triomph.kies.service.PlayerService;
import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Grid;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Player;

import java.util.List;
import java.util.Optional;

@Component
public class GridSeeder implements CommandLineRunner {

    private final GridService gridService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final PlayerService playerService;

    private static final String GRID_NAME = "TEST";

    @Autowired
    public GridSeeder(
            GridService gridService,
            CategoryService categoryService,
            AccountService accountService,
            PlayerService playerService) {
        this.gridService = gridService;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.playerService = playerService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<Grid> existingGrids = gridService.getAllGrids();
        Optional<Grid> existingAndreGrid = existingGrids.stream()
                .filter(grid -> GRID_NAME.equals(grid.getName()))
                .findFirst();
        
        if (existingAndreGrid.isPresent()) {
            System.out.println("La grille existe déjà.");
            return;
        }

        // Compte admin temp
        Player adminPlayer = playerService.getPlayerByNickname("admin")
            .orElseGet(() -> playerService.createPlayer("admin"));
            
        Account adminAccount = accountService.findAccountByPlayerId(adminPlayer.getPlayerId())
            .orElseGet(() -> accountService.createAccount(adminPlayer, "admin", 0, null));

        // Catégorie
        Category category = categoryService.getCategoryByName("Personnages")
            .orElseGet(() -> categoryService.createCategory("Personnages", adminAccount));
        
        // Grille
        Grid andreGrid = gridService.createGrid(GRID_NAME, category, adminAccount);
        andreGrid.setOfficial(true);
        andreGrid = gridService.updateGrid(andreGrid);
        
        // Ajout des perso
        String imageUrl = "/images/andre.png";
        for (int i = 1; i <= 32; i++) {
            gridService.addCharacterToGrid(andreGrid.getGridId(), "André", imageUrl, adminAccount);
        }
        
        System.out.println("Grille test créée.");
    }
} 