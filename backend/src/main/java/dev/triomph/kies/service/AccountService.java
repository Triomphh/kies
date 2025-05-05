package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.AccountDAO;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Gender;
import dev.triomph.kies.pojo.Player;


@Service
public class AccountService {

    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public List<Account> getAllAccounts() {
        return accountDAO.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountDAO.findById(id);
    }

    public Account createAccount(Player player, String password, int age, Gender gender) {
        // Check si le player a déjà un account
        if (player.getAccount() != null || findAccountByPlayerId(player.getPlayerId()).isPresent()) {
            return null;
        }
        
        Account account = new Account(player, password, age, gender);
        return accountDAO.save(account);
    }
    
    public Account createAccount(Player player, String password, int age, Gender gender, String profileImageUrl) {
        // Check si le player a déjà un account
        if (player.getAccount() != null || findAccountByPlayerId(player.getPlayerId()).isPresent()) {
            return null;
        }
        
        Account account = new Account(player, password, age, gender, profileImageUrl);
        return accountDAO.save(account);
    }

    public Account updateAccount(Account account) {
        return accountDAO.save(account);
    }

    public void deleteAccount(Long id) {
        accountDAO.deleteById(id);
    }

    public Optional<Account> findAccountByPlayerId(Long playerId) {
        return accountDAO.findByPlayerPlayerId(playerId);
    }
}