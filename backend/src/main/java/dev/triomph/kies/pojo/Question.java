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

    @Column(name = "turn_number")
    private Integer turnNumber;

    @Column(name = "text")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer")
    private Answer answer;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round", referencedColumnName = "round_id")
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asker", referencedColumnName = "player_id")
    private Player asker;
    
    // Constructeur par défaut
    public Question() {
        this.timestamp = LocalDateTime.now();
        this.answer = Answer.PENDING; // Par défaut, la réponse est en attente
    }
    
    /**
     * Crée une nouvelle question dans un round.
     *
     * @param text         Le texte de la question
     * @param turnNumber   Le numéro du tour
     * @param round        Le round associé
     * @param asker        Le joueur qui pose la question
     */
    public Question(String text, Integer turnNumber, Round round, Player asker) {
        this(); // Appel au constructeur par défaut pour initialiser le timestamp et la réponse
        this.text = text;
        this.turnNumber = turnNumber;
        this.round = round;
        this.asker = asker;
    }
    

    // Getters et Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Player getAsker() {
        return asker;
    }

    public void setAsker(Player asker) {
        this.asker = asker;
    }
}
