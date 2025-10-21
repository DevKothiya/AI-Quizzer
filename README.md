# AI Quizzer Backend

A comprehensive Spring Boot application for creating and managing AI-generated quizzes. This application leverages OpenAI's GPT models to automatically generate quiz questions, answers, and explanations based on user-specified topics and difficulty levels.

## Features

### Core Functionality
- **AI-Powered Quiz Generation**: Automatically generate quizzes using OpenAI's GPT models
- **Multiple Question Types**: Support for multiple choice, true/false, short answer, fill-in-the-blank, and essay questions
- **Difficulty Levels**: Easy, Medium, Hard, and Expert difficulty levels
- **Quiz Management**: Create, update, delete, and share quizzes
- **Quiz Attempts**: Track user quiz attempts and scores
- **Leaderboards**: View top scores for quizzes
- **Statistics**: Comprehensive analytics for quizzes and users

### Technical Features
- **RESTful API**: Well-documented REST endpoints
- **Database Integration**: JPA/Hibernate with H2 (development) and MySQL (production) support
- **Security**: Spring Security integration with password encryption
- **API Documentation**: Swagger/OpenAPI 3.0 documentation
- **Validation**: Input validation using Bean Validation
- **Error Handling**: Comprehensive error handling and responses

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring AI (OpenAI Integration)**
- **MySQL** 
- **Swagger/OpenAPI 3.0**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- OpenAI API Key
- MySQL 

## Installation and Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd ai-quizzer
```

### 2. Configure Environment Variables
Create a `.env` file or set environment variables:
```bash
export OPENAI_API_KEY=your-openai-api-key-here
```

### 3. Update Application Configuration
Edit `src/main/resources/application.yml` and update the OpenAI API key:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY:your-openai-api-key-here}
```

### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access the API documentation at:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/api-docs

## API Endpoints

### Quiz Management
- `POST /api/quizzes` - Create a new quiz
- `POST /api/quizzes/ai-generate` - Generate quiz using AI
- `GET /api/quizzes` - Get public quizzes (paginated)
- `GET /api/quizzes/{id}` - Get quiz by ID
- `GET /api/quizzes/search` - Search public quizzes
- `GET /api/quizzes/topic/{topic}` - Get quizzes by topic
- `GET /api/quizzes/difficulty/{difficulty}` - Get quizzes by difficulty
- `PUT /api/quizzes/{id}` - Update quiz
- `DELETE /api/quizzes/{id}` - Delete quiz

### Quiz Attempts
- `POST /api/quiz-attempts/start/{quizId}` - Start a quiz attempt
- `POST /api/quiz-attempts/{attemptId}/submit-answer` - Submit an answer
- `POST /api/quiz-attempts/{attemptId}/complete` - Complete quiz attempt
- `GET /api/quiz-attempts` - Get user's quiz attempts
- `GET /api/quiz-attempts/{id}` - Get quiz attempt by ID
- `GET /api/quiz-attempts/quiz/{quizId}/leaderboard` - Get quiz leaderboard

### User Management
- `POST /api/users/register` - Register a new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Example Usage

### 1. Generate a Quiz with AI
```bash
curl -X POST http://localhost:8080/api/quizzes/ai-generate \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "Java Programming",
    "difficulty": "MEDIUM",
    "numberOfQuestions": 5,
    "questionType": "MULTIPLE_CHOICE"
  }'
```

### 2. Start a Quiz Attempt
```bash
curl -X POST http://localhost:8080/api/quiz-attempts/start/1
```

### 3. Submit an Answer
```bash
curl -X POST http://localhost:8080/api/quiz-attempts/1/submit-answer \
  -H "Content-Type: application/json" \
  -d '{
    "questionId": 1,
    "userAnswer": "A"
  }'
```

### 4. Complete Quiz Attempt
```bash
curl -X POST http://localhost:8080/api/quiz-attempts/1/complete
```

## Data Models

### Quiz
- `id`: Unique identifier
- `title`: Quiz title
- `description`: Quiz description
- `topic`: Quiz topic
- `difficulty`: Difficulty level (EASY, MEDIUM, HARD, EXPERT)
- `totalQuestions`: Number of questions
- `timeLimitMinutes`: Time limit in minutes
- `isPublic`: Whether quiz is public
- `user`: Quiz creator
- `questions`: List of questions

### Question
- `id`: Unique identifier
- `content`: Question text
- `correctAnswer`: Correct answer
- `explanation`: Answer explanation
- `questionType`: Type of question (MULTIPLE_CHOICE, TRUE_FALSE, etc.)
- `points`: Points for correct answer
- `quiz`: Parent quiz
- `answers`: Answer options (for multiple choice)

### QuizAttempt
- `id`: Unique identifier
- `user`: User taking the quiz
- `quiz`: Quiz being attempted
- `startedAt`: Start time
- `completedAt`: Completion time
- `score`: Final score
- `status`: Attempt status (IN_PROGRESS, COMPLETED, etc.)

## Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 1000

server:
  port: 8080
  servlet:
    context-path: /api
```

### Database Configuration
- MySQL (configure in `application-prod.yml`)

## Security

The application includes Spring Security configuration with:
- Password encryption using BCrypt
- CORS configuration
- API endpoint security




## Roadmap

- [ ] User authentication and authorization
- [ ] Real-time quiz sessions
- [ ] Advanced analytics and reporting
- [ ] Quiz templates and categories
- [ ] Social features (sharing, comments)
- [ ] Mobile app integration
- [ ] Advanced AI features (adaptive difficulty, personalized questions)
