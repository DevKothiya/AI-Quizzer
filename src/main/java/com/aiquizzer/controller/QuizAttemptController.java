package com.aiquizzer.controller;

import com.aiquizzer.model.*;
import com.aiquizzer.service.AIQuizGenerationService;
import com.aiquizzer.service.QuizAttemptService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz-attempts")
@Tag(name = "Quiz Attempt Management", description = "APIs for managing quiz attempts")
public class QuizAttemptController {
    @Autowired
    private  QuizAttemptService quizAttemptService;
    @Autowired
    private  UserService userService;
    @Autowired
    private QuizService quizService;
    @Autowired
    private AIQuizGenerationService aiQuizGenerationService;


    
    @PostMapping("/start/{quizId}")
    @Operation(summary = "Start a quiz attempt", description = "Start a new quiz attempt for a specific quiz")
    public ResponseEntity<QuizAttempt> startQuizAttempt(@PathVariable Long quizId) {
        User user=userService.getUserById(1L).orElse(new User());
        
        try {
            QuizAttempt attempt = quizAttemptService.startQuizAttempt(quizId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(attempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{attemptId}/submit-answer")
    @Operation(summary = "Submit an answer", description = "Submit an answer for a specific question in a quiz attempt")
    public ResponseEntity<UserAnswer> submitAnswer(
            @PathVariable Long attemptId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        
        User user=userService.getUserById(1L).orElse(new User());
        try {
            UserAnswer answer = quizAttemptService.submitAnswer(
                attemptId, 
                request.getQuestionId(), 
                request.getUserAnswer(),
                user
            );
            return ResponseEntity.ok(answer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{userAnswerId}")
    public ResponseEntity<String> getHint(@PathVariable Long userAnswerId){
        UserAnswer userAnswer=quizAttemptService.getById(userAnswerId);
        Question question=userAnswer.getQuestion();
        String correct=question.getCorrectAnswer();
        String uanswer=userAnswer.getUserAnswer();
        String hint=aiQuizGenerationService.getHint(uanswer,correct,question);

        return ResponseEntity.ok(hint);
    }
    
    @PostMapping("/{attemptId}/complete")
    @Operation(summary = "Complete quiz attempt", description = "Mark a quiz attempt as completed")
    public ResponseEntity<QuizAttempt> completeQuizAttempt(@PathVariable Long attemptId) {
        User user=userService.getUserById(1L).orElse(new User());
        try {
            QuizAttempt attempt = quizAttemptService.completeQuizAttempt(attemptId, user);
            return ResponseEntity.ok(attempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/{attemptId}/abandon")
    @Operation(summary = "Abandon quiz attempt", description = "Abandon a quiz attempt")
    public ResponseEntity<QuizAttempt> abandonQuizAttempt(@PathVariable Long attemptId) {
       User user=userService.getUserById(1L).orElse(new User());
        try {
            QuizAttempt attempt = quizAttemptService.abandonQuizAttempt(attemptId, user);
            return ResponseEntity.ok(attempt);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get quiz attempt by ID", description = "Retrieve a quiz attempt by its ID")
    public ResponseEntity<QuizAttempt> getQuizAttemptById(@PathVariable Long id) {
        return quizAttemptService.getQuizAttemptById(id)
            .map(attempt -> ResponseEntity.ok(attempt))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get user's quiz attempts", description = "Retrieve all quiz attempts for the current user")
    public ResponseEntity<Page<QuizAttempt>> getUserQuizAttempts(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "startedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        User user=userService.getUserById(1L).orElse(new User());

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByUser(user, pageable);
        return ResponseEntity.ok(attempts);
    }
    
    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get quiz attempts by quiz", description = "Retrieve all attempts for a specific quiz")
    public ResponseEntity<List<QuizAttempt>> getQuizAttemptsByQuiz(@PathVariable Long quizId) {
         Quiz quiz=quizService.getQuizById(quizId).orElse(new Quiz());
        
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByQuiz(quiz);
        return ResponseEntity.ok(attempts);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get quiz attempts by status", description = "Retrieve quiz attempts by status")
    public ResponseEntity<List<QuizAttempt>> getQuizAttemptsByStatus(@PathVariable AttemptStatus status) {
        List<QuizAttempt> attempts = quizAttemptService.getQuizAttemptsByStatus(status);
        return ResponseEntity.ok(attempts);
    }
    
    @GetMapping("/{attemptId}/answers")
    @Operation(summary = "Get user answers for attempt", description = "Retrieve all user answers for a quiz attempt")
    public ResponseEntity<List<UserAnswer>> getUserAnswersByAttempt(@PathVariable Long attemptId) {
        return quizAttemptService.getQuizAttemptById(attemptId)
            .map(attempt -> {
                List<UserAnswer> answers = quizAttemptService.getUserAnswersByAttempt(attempt);
                return ResponseEntity.ok(answers);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/quiz/{quizId}/leaderboard")
    @Operation(summary = "Get quiz leaderboard", description = "Get top scores for a specific quiz")
    public ResponseEntity<List<QuizAttempt>> getQuizLeaderboard(@PathVariable Long quizId) {
        Quiz quiz=quizService.getQuizById(quizId).orElse(new Quiz());
        
        List<QuizAttempt> leaderboard = quizAttemptService.getTopScoresByQuiz(quiz);
        return ResponseEntity.ok(leaderboard);
    }
    
    @GetMapping("/quiz/{quizId}/stats")
    @Operation(summary = "Get quiz statistics", description = "Get statistics for a specific quiz")
    public ResponseEntity<Map<String, Object>> getQuizStats(@PathVariable Long quizId) {
         Quiz quiz=quizService.getQuizById(quizId).orElse(new Quiz());
        
        Map<String, Object> stats = Map.of(
            "totalAttempts", quizAttemptService.getAttemptCountByQuiz(quiz),
            "completedAttempts", quizAttemptService.getCompletedAttemptCountByQuiz(quiz),
            "averageScore", quizAttemptService.getAverageScoreByQuiz(quiz)
        );
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/user/stats")
    @Operation(summary = "Get user statistics", description = "Get statistics for the current user")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        User user=userService.getUserById(1L).orElse(new User());

        Map<String, Object> stats = Map.of(
            "totalAttempts", quizAttemptService.getAttemptCountByUser(user),
            "correctAnswers", quizAttemptService.getCorrectAnswerCountByUser(user)
        );
        
        return ResponseEntity.ok(stats);
    }
    
    // DTOs for request/response
    @Data
    public static class SubmitAnswerRequest {
        private Long questionId;
        private String userAnswer;
        
    }
}
