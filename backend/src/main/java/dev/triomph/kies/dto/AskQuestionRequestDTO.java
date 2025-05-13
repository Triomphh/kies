package dev.triomph.kies.dto;

public class AskQuestionRequestDTO {
    private Long askingPlayerId;
    private String questionText;

    public AskQuestionRequestDTO() {
    }

    public AskQuestionRequestDTO(Long askingPlayerId, String questionText) {
        this.askingPlayerId = askingPlayerId;
        this.questionText = questionText;
    }

    public Long getAskingPlayerId() {
        return askingPlayerId;
    }

    public void setAskingPlayerId(Long askingPlayerId) {
        this.askingPlayerId = askingPlayerId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}