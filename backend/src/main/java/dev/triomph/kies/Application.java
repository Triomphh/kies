package dev.triomph.kies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Gender;
import dev.triomph.kies.service.PlayerService;
import dev.triomph.kies.service.AccountService;
import java.util.Optional;

@SpringBootApplication
@RestController
public class Application {

	@Autowired
	private PlayerService playerService;
	
	@Autowired
	private AccountService accountService;

	@GetMapping("/")
	public String home() {
		return "Salut";
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		Optional<Player> existingPlayer = playerService.getPlayerByNickname("Jojo");
		
		if (existingPlayer.isEmpty()) {
			Player player = playerService.createPlayer("Jojo");
			Account account = accountService.createAccount(player, "mdp", 18, Gender.MALE);
		} else {
			System.out.println("Existe déjà");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
