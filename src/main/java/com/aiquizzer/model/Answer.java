package com.aiquizzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "answers")
@Data
public class Answer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Answer text is required")
    @Size(max = 1000, message = "Answer text must not exceed 1000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;
    
    // Constructors
    public Answer() {}
    
    public Answer(String text, Boolean isCorrect, Question question) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.question = question;
    }
    
    public Answer(String text, Boolean isCorrect, Integer orderIndex, Question question) {
        this(text, isCorrect, question);
        this.orderIndex = orderIndex;
    }

    
    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", isCorrect=" + isCorrect +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
