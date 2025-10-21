package com.aiquizzer.repository;

import com.aiquizzer.model.Question;
import com.aiquizzer.model.QuizAttempt;
import com.aiquizzer.model.User;
import com.aiquizzer.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    List<UserAnswer> findByUser(User user);
    
    List<UserAnswer> findByQuestion(Question question);
    
    List<UserAnswer> findByQuizAttempt(QuizAttempt quizAttempt);
    
    List<UserAnswer> findByUserAndQuizAttempt(User user, QuizAttempt quizAttempt);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.quizAttempt = :quizAttempt ORDER BY ua.answeredAt ASC")
    List<UserAnswer> findByQuizAttemptOrderByAnsweredAtAsc(@Param("quizAttempt") QuizAttempt quizAttempt);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.user = :user AND ua.question = :question")
    List<UserAnswer> findByUserAndQuestion(@Param("user") User user, @Param("question") Question question);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.quizAttempt = :quizAttempt AND ua.question = :question")
    Optional<UserAnswer> findByQuizAttemptAndQuestion(@Param("quizAttempt") QuizAttempt quizAttempt, @Param("question") Question question);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.quizAttempt = :quizAttempt AND ua.isCorrect = true")
    long countCorrectAnswersByQuizAttempt(@Param("quizAttempt") QuizAttempt quizAttempt);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.quizAttempt = :quizAttempt")
    long countAnswersByQuizAttempt(@Param("quizAttempt") QuizAttempt quizAttempt);
    
    @Query("SELECT SUM(ua.pointsEarned) FROM UserAnswer ua WHERE ua.quizAttempt = :quizAttempt")
    Integer sumPointsEarnedByQuizAttempt(@Param("quizAttempt") QuizAttempt quizAttempt);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.user = :user AND ua.isCorrect = true")
    List<UserAnswer> findCorrectAnswersByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(ua) FROM UserAnswer ua WHERE ua.user = :user AND ua.isCorrect = true")
    long countCorrectAnswersByUser(@Param("user") User user);
}
