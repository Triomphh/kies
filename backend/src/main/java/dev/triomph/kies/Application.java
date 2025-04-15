package dev.triomph.kies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.triomph.kies.pojo.Fake;
import dev.triomph.kies.repository.FakeRepository;


@SpringBootApplication
@RestController
public class Application {
	@Autowired
	private FakeRepository fakeRepository;

	@GetMapping("/")
	public String home() {
		return "Number: " + fakeRepository.findAll().size();
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		List<Fake> allFakes = this.fakeRepository.findAll();
		System.out.println("Number of fakes:" + allFakes.size());

		Fake newFake = new Fake();
		newFake.setFirstName("John");
		newFake.setLastName("Doe");
		this.fakeRepository.save(newFake);
		
		allFakes = this.fakeRepository.findAll();
		System.out.println("Number of fakes:" + allFakes.size());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
