package dev.triomph.kies.pojo;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asking_player_id", referencedColumnName = "player_id", nullable = false)
    private Player askingPlayer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_player_id", referencedColumnName = "player_id", nullable = false)
    private Player targetPlayer;
    
    @Column(name = "game_round_number")
    private Integer gameRoundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer")
    private Answer answer;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Constructeur par défaut
    public Question() {
        this.timestamp = LocalDateTime.now();
        this.answer = Answer.PENDING;
    }
    
    /**
     * Crée une nouvelle question.
     *
     * @param game             Le jeu associé
     * @param askingPlayer     Le joueur qui pose la question
     * @param targetPlayer     Le joueur à qui la question est posée
     * @param questionText     Le texte de la question
     * @param gameRoundNumber  Le numéro du round dans la partie
     */
    public Question(Game game, Player askingPlayer, Player targetPlayer, String questionText, Integer gameRoundNumber) {
        this();
        this.game = game;
        this.askingPlayer = askingPlayer;
        this.targetPlayer = targetPlayer;
        this.questionText = questionText;
        this.gameRoundNumber = gameRoundNumber;
    }
    

    // Getters et Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getAskingPlayer() {
        return askingPlayer;
    }

    public void setAskingPlayer(Player askingPlayer) {
        this.askingPlayer = askingPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
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

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
        if (this.answer == null) {
            this.answer = Answer.PENDING;
        }
    }
}
