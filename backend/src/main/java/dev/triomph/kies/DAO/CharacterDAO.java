package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.triomph.kies.pojo.Character;

@Repository
public interface CharacterDAO extends JpaRepository<Character, Long> { }