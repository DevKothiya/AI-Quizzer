package com.aiquizzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
public class Quiz {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Topic is required")
    @Size(max = 100, message = "Topic must not exceed 100 characters")
    @Column(nullable = false)
    private String topic;
    
    @NotNull(message = "Difficulty level is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizAttempt> quizAttempts = new ArrayList<>();
    
    // Constructors
    public Quiz() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Quiz(String title, String topic, DifficultyLevel difficulty, User user) {
        this();
        this.title = title;
        this.topic = topic;
        this.difficulty = difficulty;
        this.user = user;
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", difficulty=" + difficulty +
                ", totalQuestions=" + totalQuestions +
                ", createdAt=" + createdAt +
                '}';
    }
}
