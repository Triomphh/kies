package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.QuestionDAO;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import dev.triomph.kies.pojo.Round;
import dev.triomph.kies.pojo.Game;


@Service
public class QuestionService {

    private final QuestionDAO questionDAO;

    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public List<Question> getAllQuestions() {
        return questionDAO.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionDAO.findById(id);
    }

    public Question createQuestion(Game game, Player askingPlayer, Player targetPlayer, String questionText, Integer gameRoundNumber) {
        Question question = new Question(game, askingPlayer, targetPlayer, questionText, gameRoundNumber);
        return questionDAO.save(question);
    }
    
    public List<Question> getQuestionsByGame(Game game) {
        return questionDAO.findByGame(game);
    }


    public Question updateQuestion(Question question) {
        return questionDAO.save(question);
    }

    public void deleteQuestion(Long id) {
        questionDAO.deleteById(id);
    }
}