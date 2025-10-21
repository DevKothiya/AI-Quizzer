package com.aiquizzer.repository;

import com.aiquizzer.model.Answer;
import com.aiquizzer.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    List<Answer> findByQuestion(Question question);
    
    List<Answer> findByQuestionOrderByOrderIndex(Question question);
    
    List<Answer> findByQuestionAndIsCorrect(Question question, Boolean isCorrect);
    
    @Query("SELECT a FROM Answer a WHERE a.question = :question ORDER BY a.orderIndex ASC")
    List<Answer> findByQuestionOrderByOrderIndexAsc(@Param("question") Question question);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question = :question")
    long countByQuestion(@Param("question") Question question);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question = :question AND a.isCorrect = true")
    long countCorrectAnswersByQuestion(@Param("question") Question question);
}
