package com.aiquizzer.repository;

import com.aiquizzer.model.DifficultyLevel;
import com.aiquizzer.model.Quiz;
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
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByUser(User user);
    
    List<Quiz> findByUserAndIsPublic(User user, Boolean isPublic);
    
    List<Quiz> findByIsPublicTrue();
    
    List<Quiz> findByTopic(String topic);
    
    List<Quiz> findByDifficulty(DifficultyLevel difficulty);
    
    List<Quiz> findByTopicAndDifficulty(String topic, DifficultyLevel difficulty);
    
    @Query("SELECT q FROM Quiz q WHERE q.isPublic = true AND (q.title LIKE %:keyword% OR q.description LIKE %:keyword% OR q.topic LIKE %:keyword%)")
    Page<Quiz> findPublicQuizzesByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.isPublic = true ORDER BY q.createdAt DESC")
    Page<Quiz> findPublicQuizzesOrderByCreatedAt(Pageable pageable);
    
    @Query("SELECT q FROM Quiz q WHERE q.user = :user ORDER BY q.createdAt DESC")
    Page<Quiz> findByUserOrderByCreatedAt(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT DISTINCT q.topic FROM Quiz q WHERE q.isPublic = true")
    List<String> findDistinctTopics();
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.user = :user")
    long countByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.isPublic = true")
    long countPublicQuizzes();
    
    Optional<Quiz> findByIdAndUser(Long id, User user);
    
    Optional<Quiz> findByIdAndIsPublicTrue(Long id);
}
