package dev.triomph.kies.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Account;


public interface AccountDAO extends JpaRepository<Account, Long> { }