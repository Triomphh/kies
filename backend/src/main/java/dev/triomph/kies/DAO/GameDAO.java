package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Game;


public interface GameDAO extends JpaRepository<Game, Long> { }