package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import dev.triomph.kies.pojo.Category;


public interface CategoryDAO extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}