package com.aiquizzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Data
public class QuizAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @NotNull(message = "Quiz is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "score")
    private Double score;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "correct_answers")
    private Integer correctAnswers;
    
    @Column(name = "time_taken_seconds")
    private Long timeTakenSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;
    
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAnswer> userAnswers = new ArrayList<>();
    
    // Constructors
    public QuizAttempt() {
        this.startedAt = LocalDateTime.now();
    }
    
    public QuizAttempt(User user, Quiz quiz) {
        this();
        this.user = user;
        this.quiz = quiz;
        this.totalQuestions = quiz.getQuestions().size();
    }
    

    // Helper methods
    public void completeAttempt() {
        this.completedAt = LocalDateTime.now();
        this.status = AttemptStatus.COMPLETED;
        if (this.startedAt != null) {
            this.timeTakenSeconds = java.time.Duration.between(this.startedAt, this.completedAt).getSeconds();
        }
    }
    
    public void calculateScore() {
        if (totalQuestions != null && totalQuestions > 0) {
            this.score = (double) (correctAnswers != null ? correctAnswers : 0) / totalQuestions * 100;
        }
    }
    
    @Override
    public String toString() {
        return "QuizAttempt{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", quiz=" + (quiz != null ? quiz.getTitle() : null) +
                ", score=" + score +
                ", status=" + status +
                ", startedAt=" + startedAt +
                '}';
    }
}
