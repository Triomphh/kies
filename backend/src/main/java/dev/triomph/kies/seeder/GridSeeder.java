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
import dev.triomph.kies.pojo.Gender;

import java.util.List;
import java.util.Optional;

@Component
public class GridSeeder implements CommandLineRunner {

    private final GridService gridService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final PlayerService playerService;

    private static final String GRID_NAME = "TEST";
    private static final String POLITICIENS_GRID_NAME = "Politiciens";
    private static final String POLITICIENS_CATEGORY_NAME = "Politiciens";

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
        
        Player adminPlayer = playerService.getPlayerByNickname("admin")
            .orElseGet(() -> playerService.createPlayer("admin"));
            
        Account adminAccount = accountService.findAccountByPlayerId(adminPlayer.getPlayerId())
            .orElseGet(() -> accountService.createAccount(adminPlayer, "admin@example.com", "admin", 0, Gender.OTHER));

        if (existingAndreGrid.isPresent()) {
            System.out.println("La grille '" + GRID_NAME + "' existe déjà.");
        } else {
            // Catégorie
            Category testCategory = categoryService.getCategoryByName("Personnages")
                .orElseGet(() -> categoryService.createCategory("Personnages", adminAccount));
            
            // Grille TEST
            Grid andreGrid = gridService.createGrid(GRID_NAME, testCategory, adminAccount);
            andreGrid.setOfficial(true);
            andreGrid = gridService.updateGrid(andreGrid);
            
            // Ajout des perso
            String imageUrl = "/images/andre.png";
            for (int i = 1; i <= 32; i++) {
                gridService.addCharacterToGrid(andreGrid.getGridId(), "André", imageUrl, adminAccount);
            }
            System.out.println("Grille '" + GRID_NAME + "' créée.");
        }

        // Grille "Politiciens"
        List<Grid> currentGridsAfterTest = gridService.getAllGrids();
        Optional<Grid> existingPoliticiensGrid = currentGridsAfterTest.stream()
                .filter(grid -> POLITICIENS_GRID_NAME.equals(grid.getName()))
                .findFirst();

        if (existingPoliticiensGrid.isPresent()) {
            System.out.println("La grille '" + POLITICIENS_GRID_NAME + "' existe déjà.");
        } else {
            // Catégorie "Politiciens"
            Category politiciensCategory = categoryService.getCategoryByName(POLITICIENS_CATEGORY_NAME)
                .orElseGet(() -> categoryService.createCategory(POLITICIENS_CATEGORY_NAME, adminAccount));
            
            // Grille "Politiciens"
            Grid politiciensGrid = gridService.createGrid(POLITICIENS_GRID_NAME, politiciensCategory, adminAccount);
            politiciensGrid.setOfficial(true);
            politiciensGrid = gridService.updateGrid(politiciensGrid);
            
            // Ajout des perso à la grille "Politiciens"
            String[] politiciensImageFiles = {
                "Bayrou.png", "Bernie.png", "Biden.png", "Boris.png", "Borne.png",
                "Darmanin.png", "Edouard.png", "Fillon.png", "Harris.png", "Hidalgo.png",
                "Hollande.png", "Macron.jpeg", "Marine.png", "Melanchon.png", "Merkel.png",
                "Modi.png", "Obama.jpg", "Poutine.png", "Sarkozy.png", "Scholz.png",
                "Segolene.png", "Trudeau.png", "Trump.png", "Ursula.png", "Valls.png",
                "Vance.png", "Veran.png", "Wauquiez.png", "XiJinping.png", "xXVanceXx.png",
                "Zelensky.png", "Zemmour.png"
            };

            for (String fileName : politiciensImageFiles) {
                String characterName = fileName.substring(0, fileName.lastIndexOf('.'));
                String imageUrlPoliticiens = "/images/politiciens/" + fileName;
                gridService.addCharacterToGrid(politiciensGrid.getGridId(), characterName, imageUrlPoliticiens, adminAccount);
            }
            System.out.println("Grille '" + POLITICIENS_GRID_NAME + "' créée.");
        }
    }
} 