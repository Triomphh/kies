package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Gender;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.service.AccountService;
import dev.triomph.kies.service.PlayerService;


@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final PlayerService playerService;

    public AccountController(AccountService accountService, PlayerService playerService) {
        this.accountService = accountService;
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<Account> getAccountByPlayerId(@PathVariable Long playerId) {
        return accountService.findAccountByPlayerId(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> payload) {
        try {
            String nickname = (String) payload.get("nickname");
            String password = (String) payload.get("password");
            int age = (int) payload.get("age");
            Gender gender = Gender.valueOf((String) payload.get("gender"));

            // First check if player exists
            Player player = playerService.getPlayerByNickname(nickname)
                    .orElseGet(() -> playerService.createPlayer(nickname));

            // Check if player already has an account
            if (player.getAccount() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(null);
            }

            Account account = accountService.createAccount(player, password, age, gender);
            return ResponseEntity.status(HttpStatus.CREATED).body(account);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        if (!accountService.getAccountById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        account.setId(id); // Ensure the ID is set correctly
        return ResponseEntity.ok(accountService.updateAccount(account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (!accountService.getAccountById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}