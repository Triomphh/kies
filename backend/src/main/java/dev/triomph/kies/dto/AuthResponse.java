package dev.triomph.kies.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nickname;
    private boolean hasAccount;
    private String role;

    public AuthResponse(String token, Long id, String nickname, boolean hasAccount, String role) {
        this.token = token;
        this.id = id;
        this.nickname = nickname;
        this.hasAccount = hasAccount;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isHasAccount() {
        return hasAccount;
    }

    public void setHasAccount(boolean hasAccount) {
        this.hasAccount = hasAccount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 