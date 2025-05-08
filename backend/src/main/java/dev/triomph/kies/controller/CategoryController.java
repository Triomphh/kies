package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.triomph.kies.pojo.Category;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.service.CategoryService;
import dev.triomph.kies.service.AccountService;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AccountService accountService;

    public CategoryController(CategoryService categoryService, AccountService accountService) {
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Received category creation request: " + payload);
            
            if (!payload.containsKey("name")) {
                System.out.println("Error: Missing 'name' field in payload");
                return ResponseEntity.badRequest().build();
            }
            
            if (!payload.containsKey("creatorId")) {
                System.out.println("Error: Missing 'creatorId' field in payload");
                return ResponseEntity.badRequest().build();
            }
            
            String name = (String) payload.get("name");
            Object creatorIdObj = payload.get("creatorId");
            
            Long creatorId;
            try {
                if (creatorIdObj instanceof Integer) {
                    creatorId = ((Integer) creatorIdObj).longValue();
                } else if (creatorIdObj instanceof Long) {
                    creatorId = (Long) creatorIdObj;
                } else if (creatorIdObj instanceof String) {
                    creatorId = Long.parseLong((String) creatorIdObj);
                } else if (creatorIdObj instanceof Number) {
                    creatorId = ((Number) creatorIdObj).longValue();
                } else {
                    System.out.println("Error: Invalid creatorId type: " + creatorIdObj.getClass().getName());
                    return ResponseEntity.badRequest().build();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Failed to parse creatorId: " + creatorIdObj);
                return ResponseEntity.badRequest().build();
            }
            
            System.out.println("Looking up account for creatorId: " + creatorId);
            Optional<Account> accountOpt = accountService.getAccountById(creatorId);
            
            if (accountOpt.isEmpty()) {
                System.out.println("Error: No account found for creatorId: " + creatorId);
                return ResponseEntity.badRequest().build();
            }
            
            Account creator = accountOpt.get();
            System.out.println("Found account: " + creator.getEmail());
            
            Category category = categoryService.createCategory(name, creator);
            if (category == null) {
                System.out.println("Error: Category name '" + name + "' already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            
            System.out.println("Successfully created category: " + category.getName() + " with ID: " + category.getCategoryId());
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (Exception e) {
            System.out.println("Unexpected error in createCategory: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        if (!categoryService.getCategoryById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        category.setCategoryId(id); // pour être sûr
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryService.getCategoryById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}