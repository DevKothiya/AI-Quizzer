package com.aiquizzer.controller;

import com.aiquizzer.model.*;
import com.aiquizzer.service.QuizService;
import com.aiquizzer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/quizzes")
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create a new quiz", description = "Create a new quiz with AI-generated questions")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
         User user=userService.getUserById(1L).orElse(new User());
        Quiz quiz = quizService.createQuiz(
            request.getTitle(),
            request.getDescription(),
            request.getTopic(),
            request.getDifficulty(),
            user,
            request.getNumberOfQuestions(),
            request.getQuestionType()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(quiz);
    }
    
    @PostMapping("/ai-generate")
    @Operation(summary = "Generate quiz with AI", description = "Generate a complete quiz using AI")
    public ResponseEntity<Quiz> generateQuizWithAI(@Valid @RequestBody GenerateQuizRequest request) {
        User user=userService.getUserById(1L).orElse(new User());

            Quiz quiz = quizService.createQuizWithAI(
                    request.getTopic(),
                    request.getDifficulty(),
                    user,
                    request.getNumberOfQuestions(),
                    request.getQuestionType()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(quiz);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Retrieve a quiz by its ID")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id)
            .map(quiz -> ResponseEntity.ok(quiz))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/public/{id}")
    @Operation(summary = "Get public quiz by ID", description = "Retrieve a public quiz by its ID")
    public ResponseEntity<Quiz> getPublicQuizById(@PathVariable Long id) {
        return quizService.getPublicQuizById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get public quizzes", description = "Retrieve all public quizzes with pagination")
    public ResponseEntity<Page<Quiz>> getPublicQuizzes(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Quiz> quizzes = quizService.getPublicQuizzes(pageable);
        return ResponseEntity.ok(quizzes);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search public quizzes", description = "Search public quizzes by keyword")
    public ResponseEntity<Page<Quiz>> searchPublicQuizzes(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Quiz> quizzes = quizService.searchPublicQuizzes(keyword, pageable);
        return ResponseEntity.ok(quizzes);
    }
    
    @GetMapping("/topic/{topic}")
    @Operation(summary = "Get quizzes by topic", description = "Retrieve quizzes by topic")
    public ResponseEntity<List<Quiz>> getQuizzesByTopic(@PathVariable String topic) {
        List<Quiz> quizzes = quizService.getQuizzesByTopic(topic);
        return ResponseEntity.ok(quizzes);
    }
    
    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get quizzes by difficulty", description = "Retrieve quizzes by difficulty level")
    public ResponseEntity<List<Quiz>> getQuizzesByDifficulty(@PathVariable DifficultyLevel difficulty) {
        List<Quiz> quizzes = quizService.getQuizzesByDifficulty(difficulty);
        return ResponseEntity.ok(quizzes);
    }
    
    @GetMapping("/topics")
    @Operation(summary = "Get available topics", description = "Get list of available quiz topics")
    public ResponseEntity<List<String>> getAvailableTopics() {
        List<String> topics = quizService.getAvailableTopics();
        return ResponseEntity.ok(topics);
    }
    
    @GetMapping("/{id}/questions")
    @Operation(summary = "Get quiz questions", description = "Retrieve all questions for a quiz")
    public ResponseEntity<List<Question>> getQuizQuestions(@PathVariable Long id) {
        return quizService.getQuizById(id)
            .map(quiz -> {
                List<Question> questions = quizService.getQuestionsByQuiz(quiz);
                return ResponseEntity.ok(questions);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update quiz", description = "Update quiz details")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @Valid @RequestBody UpdateQuizRequest request) {
        User user=userService.getUserById(1L).orElse(new User());
        try {
            Quiz updatedQuiz = quizService.updateQuiz(id, request.getTitle(), request.getDescription(), request.getIsPublic(), user);
            return ResponseEntity.ok(updatedQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quiz", description = "Delete a quiz")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        User user=userService.getUserById(1L).orElse(new User());
        try {
            quizService.deleteQuiz(id, user);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get quiz statistics", description = "Get overall quiz statistics")
    public ResponseEntity<Map<String, Object>> getQuizStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPublicQuizzes", quizService.getPublicQuizCount());
        stats.put("availableTopics", quizService.getAvailableTopics().size());
        return ResponseEntity.ok(stats);
    }
    
    // DTOs for request/response
    @Data
    public static class CreateQuizRequest {
        private String title;
        private String description;
        private String topic;
        private DifficultyLevel difficulty;
        private Integer numberOfQuestions = 5;
        private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;
        

    }
    @Data
    public static class GenerateQuizRequest {
        private String topic;
        private DifficultyLevel difficulty;
        private Integer numberOfQuestions = 5;
        private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    }

    @Data
    public static class UpdateQuizRequest {
        private String title;
        private String description;
        private Boolean isPublic;

    }
}
