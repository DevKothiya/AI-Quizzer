package com.aiquizzer.repository;

import com.aiquizzer.model.AttemptStatus;
import com.aiquizzer.model.Quiz;
import com.aiquizzer.model.QuizAttempt;
import com.aiquizzer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    List<QuizAttempt> findByUser(User user);
    
    List<QuizAttempt> findByQuiz(Quiz quiz);
    
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);
    
    List<QuizAttempt> findByStatus(AttemptStatus status);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user ORDER BY qa.startedAt DESC")
    Page<QuizAttempt> findByUserOrderByStartedAtDesc(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.quiz = :quiz ORDER BY qa.score DESC")
    List<QuizAttempt> findByQuizOrderByScoreDesc(@Param("quiz") Quiz quiz);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.status = :status ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findByUserAndStatusOrderByStartedAtDesc(@Param("user") User user, @Param("status") AttemptStatus status);
    
    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.quiz = :quiz AND qa.status = 'COMPLETED'")
    Double findAverageScoreByQuiz(@Param("quiz") Quiz quiz);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz = :quiz")
    long countByQuiz(@Param("quiz") Quiz quiz);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz = :quiz AND qa.status = 'COMPLETED'")
    long countCompletedAttemptsByQuiz(@Param("quiz") Quiz quiz);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.quiz = :quiz AND qa.status = 'IN_PROGRESS'")
    Optional<QuizAttempt> findInProgressAttemptByUserAndQuiz(@Param("user") User user, @Param("quiz") Quiz quiz);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user = :user AND qa.quiz = :quiz ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findByUserAndQuizOrderByStartedAtDesc(@Param("user") User user, @Param("quiz") Quiz quiz);
}
