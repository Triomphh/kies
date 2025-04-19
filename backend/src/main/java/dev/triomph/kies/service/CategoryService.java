package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.CategoryDAO;
import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Account;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryDAO.findById(id);
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryDAO.findByName(name);
    }

    public Category createCategory(String name, Account creator) {
        // check si la catégorie existe déjà
        Optional<Category> existingCategory = categoryDAO.findByName(name);
        if (existingCategory.isPresent()) {
            return null;
        }
        
        Category category = new Category(name, creator);
        return categoryDAO.save(category);
    }

    public Category updateCategory(Category category) {
        return categoryDAO.save(category);
    }

    public void deleteCategory(Long id) {
        categoryDAO.deleteById(id);
    }
}