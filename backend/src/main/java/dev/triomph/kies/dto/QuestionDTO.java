package dev.triomph.kies.dto;

import dev.triomph.kies.pojo.Answer;
import dev.triomph.kies.pojo.Question;
import java.time.LocalDateTime;

public class QuestionDTO {
    private Long questionId;
    private Long gameId;
    private PlayerDTO askingPlayer;
    private PlayerDTO targetPlayer;
    private String questionText;
    private Integer gameRoundNumber;
    private Answer answer;
    private LocalDateTime timestamp;

    public QuestionDTO() {
    }

    public QuestionDTO(Question question) {
        this.questionId = question.getQuestionId();
        if (question.getGame() != null) {
            this.gameId = question.getGame().getGameId();
        }
        if (question.getAskingPlayer() != null) {
            this.askingPlayer = new PlayerDTO(question.getAskingPlayer());
        }
        if (question.getTargetPlayer() != null) {
            this.targetPlayer = new PlayerDTO(question.getTargetPlayer());
        }
        this.questionText = question.getQuestionText();
        this.gameRoundNumber = question.getGameRoundNumber();
        this.answer = question.getAnswer();
        this.timestamp = question.getTimestamp();
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public PlayerDTO getAskingPlayer() {
        return askingPlayer;
    }

    public void setAskingPlayer(PlayerDTO askingPlayer) {
        this.askingPlayer = askingPlayer;
    }

    public PlayerDTO getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(PlayerDTO targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getGameRoundNumber() {
        return gameRoundNumber;
    }

    public void setGameRoundNumber(Integer gameRoundNumber) {
        this.gameRoundNumber = gameRoundNumber;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}