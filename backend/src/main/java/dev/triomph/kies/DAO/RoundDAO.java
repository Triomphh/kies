package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Round;


public interface RoundDAO extends JpaRepository<Round, Long> { }