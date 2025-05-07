package dev.triomph.kies.dto;

public class PlayerNicknameUpdateRequest {
    private String nickname;
    
    public PlayerNicknameUpdateRequest() {
    }
    
    public PlayerNicknameUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
} 