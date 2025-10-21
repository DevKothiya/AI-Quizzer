package com.aiquizzer.service;

import com.aiquizzer.model.Answer;
import com.aiquizzer.model.DifficultyLevel;
import com.aiquizzer.model.Question;
import com.aiquizzer.model.QuestionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AIQuizGenerationService {
    
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AIQuizGenerationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }
    
    public List<Question> generateQuestions(String topic, DifficultyLevel difficulty, int numberOfQuestions, QuestionType questionType) {
        String prompt = buildQuestionGenerationPrompt(topic, difficulty, numberOfQuestions, questionType);
        
        Prompt aiPrompt = new Prompt(prompt);
        ChatResponse response = chatClient.prompt(aiPrompt).call().chatResponse();
        String aiResponse = response.getResult().getOutput().getContent();
        
        return parseQuestionsFromAIResponse(aiResponse, questionType);
    }
    
    public String generateQuizTitle(String topic, DifficultyLevel difficulty) {
        String prompt = String.format(
            "Generate a creative and engaging quiz title for a %s level quiz about %s. " +
            "The title should be concise (max 50 characters) and appealing. " +
            "Return only the title, no additional text.",
            difficulty.getDisplayName().toLowerCase(),
            topic
        );
        
        Prompt aiPrompt = new Prompt(prompt);
        ChatResponse response = chatClient.prompt(aiPrompt).call().chatResponse();
        return response.getResult().getOutput().getContent().trim();
    }
    
    public String generateQuizDescription(String topic, DifficultyLevel difficulty, int numberOfQuestions) {
        String prompt = String.format(
            "Generate a brief description (max 200 characters) for a %s level quiz about %s with %d questions. " +
            "The description should be engaging and informative. " +
            "Return only the description, no additional text.",
            difficulty.getDisplayName().toLowerCase(),
            topic,
            numberOfQuestions
        );
        
        Prompt aiPrompt = new Prompt(prompt);
        ChatResponse response = chatClient.prompt(aiPrompt).call().chatResponse();
        return response.getResult().getOutput().getContent().trim();
    }
    public String getHint(String userAnswer, String correctAnswer,Question question){
        String prompt="Generate a hint for "+question+" with answer "+correctAnswer+" and " +
                "user gave the answer "+userAnswer;
        Prompt aiPrompt = new Prompt(prompt);
        ChatResponse response = chatClient.prompt(aiPrompt).call().chatResponse();
        return response.getResult().getOutput().getContent().trim();

    }
    
    private String buildQuestionGenerationPrompt(String topic, DifficultyLevel difficulty, int numberOfQuestions, QuestionType questionType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate ").append(numberOfQuestions).append(" ").append(questionType.getDisplayName().toLowerCase())
              .append(" questions about ").append(topic).append(" at ").append(difficulty.getDisplayName().toLowerCase())
              .append(" difficulty level.\n\n");
        
        switch (questionType) {
            case MULTIPLE_CHOICE:
                prompt.append("For each question, provide:\n")
                      .append("1. The question text\n")
                      .append("2. Four answer options (A, B, C, D)\n")
                      .append("3. The correct answer (A, B, C, or D)\n")
                      .append("4. A brief explanation\n\n")
                      .append("Format each question as JSON:\n")
                      .append("{\n")
                      .append("  \"question\": \"Question text here\",\n")
                      .append("  \"options\": [\"Option A\", \"Option B\", \"Option C\", \"Option D\"],\n")
                      .append("  \"correctAnswer\": \"A\",\n")
                      .append("  \"explanation\": \"Explanation here\"\n")
                      .append("}\n\n");
                break;
                
            case TRUE_FALSE:
                prompt.append("For each question, provide:\n")
                      .append("1. The statement\n")
                      .append("2. Whether it's True or False\n")
                      .append("3. A brief explanation\n\n")
                      .append("Format each question as JSON:\n")
                      .append("{\n")
                      .append("  \"question\": \"Statement here\",\n")
                      .append("  \"correctAnswer\": \"True\" or \"False\",\n")
                      .append("  \"explanation\": \"Explanation here\"\n")
                      .append("}\n\n");
                break;
                
            case SHORT_ANSWER:
                prompt.append("For each question, provide:\n")
                      .append("1. The question text\n")
                      .append("2. The correct answer (short phrase or word)\n")
                      .append("3. A brief explanation\n\n")
                      .append("Format each question as JSON:\n")
                      .append("{\n")
                      .append("  \"question\": \"Question text here\",\n")
                      .append("  \"correctAnswer\": \"Correct answer here\",\n")
                      .append("  \"explanation\": \"Explanation here\"\n")
                      .append("}\n\n");
                break;
                
            case FILL_IN_BLANK:
                prompt.append("For each question, provide:\n")
                      .append("1. A sentence with a blank (use _____ for the blank)\n")
                      .append("2. The correct word/phrase for the blank\n")
                      .append("3. A brief explanation\n\n")
                      .append("Format each question as JSON:\n")
                      .append("{\n")
                      .append("  \"question\": \"Sentence with _____ here\",\n")
                      .append("  \"correctAnswer\": \"Correct word/phrase\",\n")
                      .append("  \"explanation\": \"Explanation here\"\n")
                      .append("}\n\n");
                break;
        }
        
        prompt.append("Return the questions as a JSON array. Make sure the questions are educational, accurate, and appropriate for the difficulty level.");
        
        return prompt.toString();
    }
    
    private List<Question> parseQuestionsFromAIResponse(String aiResponse, QuestionType questionType) {
        List<Question> questions = new ArrayList<>();
        
        try {
            // Clean the response to extract JSON array
            String jsonArray = extractJsonArray(aiResponse);
            JsonNode questionsNode = objectMapper.readTree(jsonArray);
            
            if (questionsNode.isArray()) {
                for (JsonNode questionNode : questionsNode) {
                    Question question = parseQuestionFromJson(questionNode, questionType);
                    if (question != null) {
                        questions.add(question);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            // Fallback: try to parse individual questions
            questions = parseQuestionsFromText(aiResponse, questionType);
        }
        
        return questions;
    }
    
    private String extractJsonArray(String response) {
        // Try to find JSON array in the response
        int startIndex = response.indexOf('[');
        int endIndex = response.lastIndexOf(']');
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }
        
        return response;
    }
    
    private Question parseQuestionFromJson(JsonNode questionNode, QuestionType questionType) {
        try {
            String content = questionNode.get("question").asText();
            String correctAnswer = questionNode.get("correctAnswer").asText();
            String explanation = questionNode.has("explanation") ? questionNode.get("explanation").asText() : "";
            
            Question question = new Question();
            question.setContent(content);
            question.setCorrectAnswer(correctAnswer);
            question.setExplanation(explanation);
            question.setQuestionType(questionType);
            question.setPoints(1);
            
            // For multiple choice questions, create answer options
            if (questionType == QuestionType.MULTIPLE_CHOICE && questionNode.has("options")) {
                JsonNode optionsNode = questionNode.get("options");
                if (optionsNode.isArray()) {
                    List<Answer> answers = new ArrayList<>();
                    String[] optionLabels = {"A", "B", "C", "D"};
                    
                    for (int i = 0; i < optionsNode.size() && i < 4; i++) {
                        Answer answer = new Answer();
                        answer.setText(optionsNode.get(i).asText());
                        answer.setIsCorrect(optionLabels[i].equals(correctAnswer));
                        answer.setOrderIndex(i);
                        answers.add(answer);
                    }
                    question.setAnswers(answers);
                }
            }
            
            return question;
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<Question> parseQuestionsFromText(String response, QuestionType questionType) {
        // Fallback parsing for when JSON parsing fails
        List<Question> questions = new ArrayList<>();
        String[] lines = response.split("\n");
        
        Question currentQuestion = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            if (line.startsWith("Question:") || line.startsWith("Q:")) {
                if (currentQuestion != null) {
                    questions.add(currentQuestion);
                }
                currentQuestion = new Question();
                currentQuestion.setContent(line.replaceFirst("^(Question:|Q:)\\s*", ""));
                currentQuestion.setQuestionType(questionType);
                currentQuestion.setPoints(1);
            } else if (line.startsWith("Answer:") || line.startsWith("Correct Answer:")) {
                if (currentQuestion != null) {
                    currentQuestion.setCorrectAnswer(line.replaceFirst("^(Answer:|Correct Answer:)\\s*", ""));
                }
            } else if (line.startsWith("Explanation:")) {
                if (currentQuestion != null) {
                    currentQuestion.setExplanation(line.replaceFirst("^Explanation:\\s*", ""));
                }
            }
        }
        
        if (currentQuestion != null) {
            questions.add(currentQuestion);
        }
        
        return questions;
    }
}
