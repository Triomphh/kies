package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Grid;


public interface GridDAO extends JpaRepository<Grid, Long> { }