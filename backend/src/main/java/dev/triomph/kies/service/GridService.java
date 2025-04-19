package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.GridDAO;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Grid;

@Service
public class GridService {

    private final GridDAO gridDAO;

    public GridService(GridDAO gridDAO) {
        this.gridDAO = gridDAO;
    }

    public List<Grid> getAllGrids() {
        return gridDAO.findAll();
    }

    public Optional<Grid> getGridById(Long id) {
        return gridDAO.findById(id);
    }

    public Grid createGrid(String name, Category category, String imageUrl, Integer rows, Integer columns,
                           Integer x, Integer y, Integer height, Integer width, Integer gapX, Integer gapY,
                           Account creator) {
        Grid grid = new Grid(name, category, imageUrl, rows, columns, x, y, width, height, gapX, gapY, creator);
        return gridDAO.save(grid);
    }

    public Grid updateGrid(Grid grid) {
        return gridDAO.save(grid);
    }

    public void deleteGrid(Long id) {
        gridDAO.deleteById(id);
    }
}