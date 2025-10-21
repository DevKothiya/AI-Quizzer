package com.aiquizzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_answers")
@Data
public class UserAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @NotNull(message = "Question is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;
    
    @NotNull(message = "Quiz attempt is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    @JsonIgnore
    private QuizAttempt quizAttempt;
    
    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "points_earned")
    private Integer pointsEarned = 0;
    
    @Column(name = "time_taken_seconds")
    private Long timeTakenSeconds;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    // Constructors
    public UserAnswer() {
        this.answeredAt = LocalDateTime.now();
    }
    
    public UserAnswer(User user, Question question, QuizAttempt quizAttempt, String userAnswer) {
        this();
        this.user = user;
        this.question = question;
        this.quizAttempt = quizAttempt;
        this.userAnswer = userAnswer;
    }

    // Helper methods
    public void checkAnswer() {
        if (userAnswer != null && question != null && question.getCorrectAnswer() != null) {
            this.isCorrect = userAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim());
            this.pointsEarned = isCorrect ? (question.getPoints() != null ? question.getPoints() : 1) : 0;
        } else {
            this.isCorrect = false;
            this.pointsEarned = 0;
        }
    }
    
    @Override
    public String toString() {
        return "UserAnswer{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", question=" + (question != null ? question.getId() : null) +
                ", userAnswer='" + userAnswer + '\'' +
                ", isCorrect=" + isCorrect +
                ", pointsEarned=" + pointsEarned +
                ", answeredAt=" + answeredAt +
                '}';
    }
}
