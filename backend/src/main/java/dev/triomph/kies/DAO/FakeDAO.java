package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Fake;


public interface FakeDAO extends JpaRepository<Fake, Long> { }
