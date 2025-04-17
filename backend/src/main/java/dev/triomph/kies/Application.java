package dev.triomph.kies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.triomph.kies.DAO.FakeDAO;
import dev.triomph.kies.pojo.Fake;


@SpringBootApplication
@RestController
public class Application {
	@Autowired
	private FakeDAO fakeDAO;

	@GetMapping("/")
	public String home() {
		return "Number: " + fakeDAO.findAll().size();
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		List<Fake> allFakes = this.fakeDAO.findAll();
		System.out.println("Number of fakes:" + allFakes.size());

		Fake newFake = new Fake();
		newFake.setFirstName("John");
		newFake.setLastName("Doe");
		this.fakeDAO.save(newFake);
		
		allFakes = this.fakeDAO.findAll();
		System.out.println("Number of fakes:" + allFakes.size());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
