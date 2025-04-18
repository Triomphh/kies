package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Question;


public interface QuestionDAO extends JpaRepository<Question, Long> { }