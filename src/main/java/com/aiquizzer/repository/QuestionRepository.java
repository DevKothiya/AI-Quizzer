package com.aiquizzer.repository;

import com.aiquizzer.model.Question;
import com.aiquizzer.model.QuestionType;
import com.aiquizzer.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByQuiz(Quiz quiz);
    
    List<Question> findByQuizOrderById(Quiz quiz);
    
    List<Question> findByQuestionType(QuestionType questionType);
    
    @Query("SELECT q FROM Question q WHERE q.quiz = :quiz ORDER BY q.id")
    List<Question> findByQuizOrderByIdAsc(@Param("quiz") Quiz quiz);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz = :quiz")
    long countByQuiz(@Param("quiz") Quiz quiz);
    
    @Query("SELECT q FROM Question q WHERE q.quiz = :quiz AND q.questionType = :questionType")
    List<Question> findByQuizAndQuestionType(@Param("quiz") Quiz quiz, @Param("questionType") QuestionType questionType);
    
    @Query("SELECT q FROM Question q WHERE q.content LIKE %:keyword%")
    List<Question> findByContentContaining(@Param("keyword") String keyword);
    
    @Query("SELECT q FROM Question q WHERE q.quiz = :quiz AND q.content LIKE %:keyword%")
    List<Question> findByQuizAndContentContaining(@Param("quiz") Quiz quiz, @Param("keyword") String keyword);
}
