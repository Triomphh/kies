package dev.triomph.kies.dto;

import dev.triomph.kies.pojo.Answer;

public class AnswerDTO {
    private Long questionId;
    private Answer answer;
    private Long answeringPlayerId;

    public AnswerDTO() {
    }

    public AnswerDTO(Long questionId, Answer answer, Long answeringPlayerId) {
        this.questionId = questionId;
        this.answer = answer;
        this.answeringPlayerId = answeringPlayerId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Long getAnsweringPlayerId() {
        return answeringPlayerId;
    }

    public void setAnsweringPlayerId(Long answeringPlayerId) {
        this.answeringPlayerId = answeringPlayerId;
    }
}