package dev.triomph.kies.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.triomph.kies.DAO.QuestionDAO;
import dev.triomph.kies.pojo.Player;
import dev.triomph.kies.pojo.Question;
import dev.triomph.kies.pojo.Round;


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

    public Question createQuestion(String text, Integer turnNumber, Round round, Player asker) {
        Question question = new Question(text, turnNumber, round, asker);
        return questionDAO.save(question);
    }

    public Question updateQuestion(Question question) {
        return questionDAO.save(question);
    }

    public void deleteQuestion(Long id) {
        questionDAO.deleteById(id);
    }
}