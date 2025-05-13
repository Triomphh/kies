package dev.triomph.kies.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private String type;
    private String content;
    private Long senderId;
    private String senderNickname;
    private LocalDateTime timestamp;
    private Long gameId;

    public ChatMessageDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessageDTO(String type, String content, Long senderId, String senderNickname, Long gameId) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.timestamp = LocalDateTime.now();
        this.gameId = gameId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}