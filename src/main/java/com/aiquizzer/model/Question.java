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
@Table(name = "questions")
@Data
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Question content is required")
    @Size(max = 2000, message = "Question content must not exceed 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @NotBlank(message = "Correct answer is required")
    @Size(max = 500, message = "Correct answer must not exceed 500 characters")
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;
    
    @Size(max = 1000, message = "Explanation must not exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String explanation;
    
    @NotNull(message = "Question type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;
    
    @Column(name = "points")
    private Integer points = 1;
    
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Answer> answers = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAnswer> userAnswers = new ArrayList<>();

    // Constructors
    public Question() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Question(String content, String correctAnswer, QuestionType questionType, Quiz quiz) {
        this();
        this.content = content;
        this.correctAnswer = correctAnswer;
        this.questionType = questionType;
        this.quiz = quiz;
    }
    

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", questionType=" + questionType +
                ", points=" + points +
                ", createdAt=" + createdAt +
                '}';
    }
}
