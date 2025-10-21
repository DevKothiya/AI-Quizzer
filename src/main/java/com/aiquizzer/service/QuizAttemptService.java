package com.aiquizzer.service;

import com.aiquizzer.model.*;
import com.aiquizzer.repository.QuizAttemptRepository;
import com.aiquizzer.repository.QuizRepository;
import com.aiquizzer.repository.UserAnswerRepository;
import com.aiquizzer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private QuizRepository quizRepository;
    
    public QuizAttempt startQuizAttempt(Long quizId, User user) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found");
        }
        
        Quiz quiz = quizOpt.get();
        
        // Check if user already has an in-progress attempt
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository
            .findInProgressAttemptByUserAndQuiz(user, quiz);
        
        if (existingAttempt.isPresent()) {
            return existingAttempt.get();
        }
        
        QuizAttempt attempt = new QuizAttempt(user, quiz);
        return quizAttemptRepository.save(attempt);
    }
    
    public UserAnswer submitAnswer(Long attemptId, Long questionId, String userAnswer, User user) {
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findById(attemptId);
        if (attemptOpt.isEmpty()) {
            throw new RuntimeException("Quiz attempt not found");
        }
        
        QuizAttempt attempt = attemptOpt.get();
        
        // Verify the attempt belongs to the user
        if (!attempt.getUser().equals(user)) {
            throw new RuntimeException("Access denied");
        }
        
        // Check if attempt is still in progress
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz attempt is not in progress");
        }
        
        // Check if answer already exists
        Optional<UserAnswer> existingAnswer = userAnswerRepository
            .findByQuizAttemptAndQuestion(attempt, attempt.getQuiz().getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz")));
        
        UserAnswer answer;
        if (existingAnswer.isPresent()) {
            answer = existingAnswer.get();
            answer.setUserAnswer(userAnswer);
        } else {
            Question question = attempt.getQuiz().getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz"));
            
            answer = new UserAnswer(user, question, attempt, userAnswer);
        }
        
        // Check the answer and calculate points
        answer.checkAnswer();
        
        UserAnswer savedAnswer = userAnswerRepository.save(answer);
        
        // Update attempt statistics
        updateAttemptStatistics(attempt);
        
        return savedAnswer;
    }
    
    public QuizAttempt completeQuizAttempt(Long attemptId, User user) {
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findById(attemptId);
        if (!attemptOpt.isPresent()) {
            throw new RuntimeException("Quiz attempt not found");
        }
        
        QuizAttempt attempt = attemptOpt.get();
        
        // Verify the attempt belongs to the user
        if (!attempt.getUser().equals(user)) {
            throw new RuntimeException("Access denied");
        }
        
        // Check if attempt is still in progress
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new RuntimeException("Quiz attempt is not in progress");
        }
        
        // Complete the attempt
        attempt.completeAttempt();
        attempt.calculateScore();
        
        return quizAttemptRepository.save(attempt);
    }
    
    public QuizAttempt abandonQuizAttempt(Long attemptId, User user) {
        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findById(attemptId);
        if (!attemptOpt.isPresent()) {
            throw new RuntimeException("Quiz attempt not found");
        }
        
        QuizAttempt attempt = attemptOpt.get();
        
        // Verify the attempt belongs to the user
        if (!attempt.getUser().equals(user)) {
            throw new RuntimeException("Access denied");
        }
        
        // Abandon the attempt
        attempt.setStatus(AttemptStatus.ABANDONED);
        attempt.setCompletedAt(LocalDateTime.now());
        
        return quizAttemptRepository.save(attempt);
    }
    
    public Optional<QuizAttempt> getQuizAttemptById(Long id) {
        return quizAttemptRepository.findById(id);
    }
    
    public List<QuizAttempt> getQuizAttemptsByUser(User user) {
        return quizAttemptRepository.findByUser(user);
    }
    
    public Page<QuizAttempt> getQuizAttemptsByUser(User user, Pageable pageable) {
        return quizAttemptRepository.findByUserOrderByStartedAtDesc(user, pageable);
    }
    
    public List<QuizAttempt> getQuizAttemptsByQuiz(Quiz quiz) {
        return quizAttemptRepository.findByQuiz(quiz);
    }
    
    public List<QuizAttempt> getQuizAttemptsByUserAndQuiz(User user, Quiz quiz) {
        return quizAttemptRepository.findByUserAndQuiz(user, quiz);
    }
    
    public List<QuizAttempt> getQuizAttemptsByStatus(AttemptStatus status) {
        return quizAttemptRepository.findByStatus(status);
    }
    
    public List<QuizAttempt> getQuizAttemptsByUserAndStatus(User user, AttemptStatus status) {
        return quizAttemptRepository.findByUserAndStatusOrderByStartedAtDesc(user, status);
    }
    
    public List<QuizAttempt> getTopScoresByQuiz(Quiz quiz) {
        return quizAttemptRepository.findByQuizOrderByScoreDesc(quiz);
    }
    
    public Double getAverageScoreByQuiz(Quiz quiz) {
        return quizAttemptRepository.findAverageScoreByQuiz(quiz);
    }
    
    public long getAttemptCountByQuiz(Quiz quiz) {
        return quizAttemptRepository.countByQuiz(quiz);
    }
    
    public long getAttemptCountByUser(User user) {
        return quizAttemptRepository.countByUser(user);
    }
    
    public long getCompletedAttemptCountByQuiz(Quiz quiz) {
        return quizAttemptRepository.countCompletedAttemptsByQuiz(quiz);
    }
    
    public List<UserAnswer> getUserAnswersByAttempt(QuizAttempt attempt) {
        return userAnswerRepository.findByQuizAttemptOrderByAnsweredAtAsc(attempt);
    }
    
    public List<UserAnswer> getUserAnswersByUser(User user) {
        return userAnswerRepository.findByUser(user);
    }
    
    public List<UserAnswer> getCorrectAnswersByUser(User user) {
        return userAnswerRepository.findCorrectAnswersByUser(user);
    }
    
    public long getCorrectAnswerCountByUser(User user) {
        return userAnswerRepository.countCorrectAnswersByUser(user);
    }
    
    private void updateAttemptStatistics(QuizAttempt attempt) {
        List<UserAnswer> answers = userAnswerRepository.findByQuizAttempt(attempt);
        
        long correctCount = answers.stream()
            .mapToLong(answer -> answer.getIsCorrect() ? 1 : 0)
            .sum();
        
        attempt.setCorrectAnswers((int) correctCount);
        attempt.calculateScore();
        
        quizAttemptRepository.save(attempt);
    }

    public UserAnswer getById(Long id){
        return userAnswerRepository.findById(id).orElse(null);
    }
}
