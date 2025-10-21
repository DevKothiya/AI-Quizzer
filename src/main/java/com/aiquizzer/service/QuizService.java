package com.aiquizzer.service;

import com.aiquizzer.model.*;
import com.aiquizzer.repository.AnswerRepository;
import com.aiquizzer.repository.QuestionRepository;
import com.aiquizzer.repository.QuizRepository;
import com.aiquizzer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AIQuizGenerationService aiQuizGenerationService;
    

    
    public Quiz createQuiz(String title, String description, String topic,
                           DifficultyLevel difficulty, User user,
                           int numberOfQuestions, QuestionType questionType) {
        
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setTopic(topic);
        quiz.setDifficulty(difficulty);
        quiz.setUser(user);
        quiz.setTotalQuestions(numberOfQuestions);
        quiz.setIsPublic(false);
        
        Quiz savedQuiz = quizRepository.save(quiz);
        // Generate questions using AI
        List<Question> generatedQuestions = aiQuizGenerationService.generateQuestions(
            topic, difficulty, numberOfQuestions, questionType
        );

        for (Question question : generatedQuestions) {
            question.setQuiz(savedQuiz);
            // Ensure each answer knows its question
            if (question.getAnswers() != null) {
                for (Answer answer : question.getAnswers()) {
                    answer.setQuestion(question);
                }
            }
            questionRepository.save(question); // saves question and all answers
        }
        
        return savedQuiz;
    }
    
    public Quiz createQuizWithAI(String topic, DifficultyLevel difficulty, User user, 
                                int numberOfQuestions, QuestionType questionType) {
        
        // Generate title and description using AI
        String title = aiQuizGenerationService.generateQuizTitle(topic, difficulty);
        String description = aiQuizGenerationService.generateQuizDescription(topic, difficulty, numberOfQuestions);

        return createQuiz(title, description, topic, difficulty, user, numberOfQuestions, questionType);
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    public Optional<Quiz> getQuizByIdAndUser(Long id, User user) {
        return quizRepository.findByIdAndUser(id, user);
    }
    
    public Optional<Quiz> getPublicQuizById(Long id) {
        return quizRepository.findByIdAndIsPublicTrue(id);
    }
    
    public List<Quiz> getQuizzesByUser(User user) {
        return quizRepository.findByUser(user);
    }
    
    public Page<Quiz> getQuizzesByUser(User user, Pageable pageable) {
        return quizRepository.findByUserOrderByCreatedAt(user, pageable);
    }
    
    public List<Quiz> getPublicQuizzes() {
        return quizRepository.findByIsPublicTrue();
    }
    
    public Page<Quiz> getPublicQuizzes(Pageable pageable) {
        return quizRepository.findPublicQuizzesOrderByCreatedAt(pageable);
    }
    
    public Page<Quiz> searchPublicQuizzes(String keyword, Pageable pageable) {
        return quizRepository.findPublicQuizzesByKeyword(keyword, pageable);
    }
    
    public List<Quiz> getQuizzesByTopic(String topic) {
        return quizRepository.findByTopic(topic);
    }
    
    public List<Quiz> getQuizzesByDifficulty(DifficultyLevel difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }
    
    public List<Quiz> getQuizzesByTopicAndDifficulty(String topic, DifficultyLevel difficulty) {
        return quizRepository.findByTopicAndDifficulty(topic, difficulty);
    }
    
    public List<String> getAvailableTopics() {
        return quizRepository.findDistinctTopics();
    }
    
    public Quiz updateQuiz(Long id, String title, String description, Boolean isPublic, User user) {
        Optional<Quiz> quizOpt = quizRepository.findByIdAndUser(id, user);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            if (title != null) quiz.setTitle(title);
            if (description != null) quiz.setDescription(description);
            if (isPublic != null) quiz.setIsPublic(isPublic);
            return quizRepository.save(quiz);
        }
        throw new RuntimeException("Quiz not found or access denied");
    }
    
    public void deleteQuiz(Long id, User user) {
        Optional<Quiz> quizOpt = quizRepository.findByIdAndUser(id, user);
        if (quizOpt.isPresent()) {
            quizRepository.delete(quizOpt.get());
        } else {
            throw new RuntimeException("Quiz not found or access denied");
        }
    }
    
    public List<Question> getQuestionsByQuiz(Quiz quiz) {
        return questionRepository.findByQuizOrderById(quiz);
    }
    
    public Question addQuestionToQuiz(Long quizId, String content, String correctAnswer, 
                                    QuestionType questionType, User user) {
        Optional<Quiz> quizOpt = quizRepository.findByIdAndUser(quizId, user);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            Question question = new Question(content, correctAnswer, questionType, quiz);
            return questionRepository.save(question);
        }
        throw new RuntimeException("Quiz not found or access denied");
    }
    
    public void deleteQuestion(Long questionId, User user) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isPresent()) {
            Question question = questionOpt.get();
            if (question.getQuiz().getUser().equals(user)) {
                questionRepository.delete(question);
            } else {
                throw new RuntimeException("Access denied");
            }
        } else {
            throw new RuntimeException("Question not found");
        }
    }
    
    public long getQuizCountByUser(User user) {
        return quizRepository.countByUser(user);
    }
    
    public long getPublicQuizCount() {
        return quizRepository.countPublicQuizzes();
    }
    
    public boolean isQuizOwner(Long quizId, User user) {
        return quizRepository.findByIdAndUser(quizId, user).isPresent();
    }
}
