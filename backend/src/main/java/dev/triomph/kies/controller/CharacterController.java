package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Account;
import dev.triomph.kies.pojo.Character;
import dev.triomph.kies.service.AccountService;
import dev.triomph.kies.service.CharacterService;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {

    private final CharacterService characterService;
    private final AccountService accountService;

    public CharacterController(CharacterService characterService, AccountService accountService) {
        this.characterService = characterService;
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<Character>> getAllCharacters() {
        return ResponseEntity.ok(characterService.getAllCharacters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Character> getCharacterById(@PathVariable Long id) {
        return characterService.getCharacterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Character> createCharacter(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String imageUrl = (String) payload.get("imageUrl");
            Long creatorId = Long.parseLong(payload.get("creatorId").toString());

            Optional<Account> creatorOpt = accountService.getAccountById(creatorId);
            
            if (creatorOpt.isPresent()) {
                Character character = characterService.createCharacter(name, imageUrl, creatorOpt.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(character);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Character> updateCharacter(@PathVariable Long id, @RequestBody Character character) {
        if (!characterService.getCharacterById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        character.setCharacterId(id);
        return ResponseEntity.ok(characterService.updateCharacter(character));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        if (!characterService.getCharacterById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        characterService.deleteCharacter(id);
        return ResponseEntity.noContent().build();
    }
}