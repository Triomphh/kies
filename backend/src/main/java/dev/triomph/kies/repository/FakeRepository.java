package dev.triomph.kies.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Fake;


public interface FakeRepository extends JpaRepository<Fake, Long> { }
