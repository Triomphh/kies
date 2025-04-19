package dev.triomph.kies.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import dev.triomph.kies.pojo.Round;
import dev.triomph.kies.service.PlayerService;
import dev.triomph.kies.service.QuestionService;
import dev.triomph.kies.service.RoundService;


@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final PlayerService playerService;
    private final RoundService roundService;

    public QuestionController(QuestionService questionService, PlayerService playerService, RoundService roundService) {
        this.questionService = questionService;
        this.playerService = playerService;
        this.roundService = roundService;
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Map<String, Object> payload) {
        try {
            String text = (String) payload.get("text");
            Integer turnNumber = (Integer) payload.get("turnNumber");
            Long roundId = Long.parseLong(payload.get("roundId").toString());
            Long askerId = Long.parseLong(payload.get("askerId").toString());

            Optional<Round> roundOpt = roundService.getRoundById(roundId);
            Optional<Player> askerOpt = playerService.getPlayerById(askerId);

            if (roundOpt.isPresent() && askerOpt.isPresent()) {
                Question question = questionService.createQuestion(
                    text,
                    turnNumber,
                    roundOpt.get(),
                    askerOpt.get()
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(question);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        if (!questionService.getQuestionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        question.setQuestionId(id);
        return ResponseEntity.ok(questionService.updateQuestion(question));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        if (!questionService.getQuestionById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }


    // @PutMapping("/{id}/answer")
    // answerQuestion()               ...à implémenter
}