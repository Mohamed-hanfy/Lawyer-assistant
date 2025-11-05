# Legal Case Management System

A comprehensive Spring Boot application designed for Egyptian lawyers to manage lawsuits, analyze legal documents, and communicate with clients efficiently using AI-powered document analysis and automated WhatsApp notifications.

## Overview

This system provides an end-to-end solution for legal practice management in Egypt, integrating modern technologies like AI document analysis, cloud storage, and real-time client communication. The platform streamlines case management workflows, automates document analysis using specialized legal AI prompts, and maintains secure, organized case files with Google Drive integration.

## Features

### 🔐 **Authentication & Security**
- **JWT-based Authentication**: Stateless authentication using JSON Web Tokens for secure API access
- **Spring Security Integration**: Role-based access control with custom security configurations
- **Lawyer Account Management**: Secure registration and authentication for legal professionals

### 📁 **Case (Lawsuit) Management**
- **Full CRUD Operations**: Create, read, update, and delete lawsuits
- **Status Tracking**: Monitor case progression through different stages (ACTIVE, PENDING, CLOSED, etc.)
- **Client Association**: Link cases to specific clients with detailed information
- **Soft Delete**: Mark cases as deleted without permanent removal from database
- **Advanced Search**:
  - Fuzzy search for Arabic names using Levenshtein distance
  - Search by case name, client name, or status
  - Arabic text normalization for accurate matching
- **Google Drive Integration**: Automatic folder creation for each lawsuit in Google Drive

### 📄 **Document Management**
- **Multi-format Support**: PDF document upload and management
- **Google Drive Storage**: Secure cloud storage with OAuth2 authentication
- **Document Metadata**: Track document names, descriptions, and associations with lawsuits
- **File Operations**:
  - Upload documents with automatic Google Drive backup
  - Download documents from cloud storage
  - Delete documents with cascade cleanup
  - Retrieve file metadata

### 🤖 **AI-Powered Legal Analysis**
- **Ollama Integration**: Local AI model (Qwen 3 1.7B) for privacy-preserving analysis
- **Specialized Legal Prompts**: Three comprehensive prompt specifications for Egyptian law:
  1. **Document Summarization**: Extract key information (parties, dates, claims, legal references)
  2. **Deep Legal Analysis**: Risk assessment, compliance checking, legal strength evaluation
  3. **Case Timeline Analysis**: Event reconstruction, pattern recognition, next steps recommendation
- **PDF Text Extraction**: Apache PDFBox integration for document parsing
- **Streaming Responses**: Real-time AI analysis results using reactive streams
- **Context-Aware Analysis**: AI understands Egyptian legal system and terminology

### 📝 **Case Logs & Activity Tracking**
- **Event Logging**: Track all case-related activities and milestones
- **Automated Notifications**: WhatsApp integration for client updates
- **Timeline Construction**: Build chronological case histories
- **Log Analysis**: AI-powered analysis of case progression and recommendations

### 💬 **WhatsApp Integration**
- **Automated Client Notifications**: Real-time updates sent to clients via WhatsApp
- **Arabic Message Support**: Full UTF-8 Arabic text support
- **Reactive Communication**: Non-blocking message delivery using WebFlux
- **Notification Templates**: Pre-formatted messages for case updates

### 🔍 **Advanced Search & Filtering**
- **Arabic Text Normalization**: Handle diacritics, variations, and special characters
- **Fuzzy Matching**: Find similar names despite spelling variations
- **Multi-criteria Filtering**: Combine status, client name, and case name filters
- **Similarity Scoring**: Rank results by relevance

### 📊 **API Documentation**
- **OpenAPI/Swagger Integration**: Interactive API documentation
- **CSRF Protection**: Enabled in Swagger UI for security
- **Comprehensive Endpoints**: Well-documented REST API for all operations

## Project Architecture

### Technology Stack

#### Backend Framework
- **Spring Boot 3.4.9**: Modern Java framework with auto-configuration
- **Java 24**: Latest Java features including virtual threads
- **Maven**: Dependency management and build automation

#### Data Layer
- **Spring Data JPA**: Object-relational mapping and database operations
- **MySQL**: Primary relational database for structured data
- **Hibernate**: ORM with automatic schema updates
- **H2 Database**: In-memory database for testing

#### Security
- **Spring Security**: Comprehensive security framework
- **JWT (JSON Web Tokens)**: JJWT library (v0.11.5) for token generation/validation
- **Google OAuth2**: OAuth2 authentication for Google Drive API

#### AI & Machine Learning
- **Spring AI Framework**: Official Spring AI integration (v1.0.0-M1)
- **Ollama**: Local LLM deployment for privacy-preserving AI analysis
- **Qwen 3 (1.7B)**: Lightweight multilingual model optimized for legal text

#### Document Processing
- **Apache PDFBox 3.0.5**: PDF parsing and text extraction
- **Google Drive API v3**: Cloud storage integration with file operations

#### Communication
- **Spring WebFlux**: Reactive programming for non-blocking WhatsApp API calls
- **WebClient**: Modern HTTP client for external API integration

#### Utilities
- **Lombok**: Reduce boilerplate code with annotations
- **Apache Commons Text 1.14.0**: String similarity algorithms (Levenshtein distance)
- **Jakarta Validation**: Bean validation for request DTOs

#### DevOps & Deployment
- **Docker**: Multi-stage Dockerfile for optimized container images
- **Docker Compose**: Orchestration with MySQL and application services
- **Eclipse Temurin**: OpenJDK distribution for runtime

#### Documentation
- **SpringDoc OpenAPI (v2.8.9)**: Automated API documentation generation

### Architecture Patterns

#### 1. **Layered Architecture**
```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (Controllers, DTOs, Requests)      │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│      Service Layer                  │
│  (Business Logic, Validation)       │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│     Data Access Layer               │
│  (Repositories, Entities, JPA)      │
└─────────────────────────────────────┘
```

#### 2. **Security Configuration**
- **JWT Authentication Filter**: Custom filter chain for token validation
- **Stateless Session Management**: No server-side session storage
- **Role-Based Access Control**: Lawyer-specific endpoints
- **Password Encoding**: BCrypt hashing for secure password storage

#### 3. **External Integrations**
- **Google Drive Service**: OAuth2 flow with credential management
  - Token persistence with automatic refresh
  - File upload/download operations
  - Folder hierarchy management
- **WhatsApp API**: Reactive HTTP client for message delivery
- **Ollama AI Service**: Local model inference with streaming responses

#### 4. **Domain Model**
```
Lawyer (1) ──────┐
                 │
                 │ (1:N)
                 │
                 ▼
             Lawsuit (1) ────┬────> Logs (N)
                 │           │
                 │ (1:N)     │
                 ▼           │
              Docs (N) ──────┘
```

#### 5. **Configuration Management**
- **Environment Variables**: `.env` file for sensitive configuration
- **Application Profiles**: Development, production, and test profiles
- **Externalized Configuration**: Docker-compatible environment variables

#### 6. **Error Handling**
- **Global Exception Handler**: Centralized exception management
- **Custom Exception Responses**: Structured error responses with HTTP status codes
- **Validation Error Handling**: Jakarta validation with detailed error messages

#### 7. **Reactive Programming**
- **Flux Streams**: Server-sent events for AI analysis results
- **Mono Operations**: Non-blocking WhatsApp message delivery
- **WebFlux Integration**: Reactive HTTP client for external APIs

#### 8. **AI Prompt Engineering**
- **System Prompts**: Egyptian legal system context and guidelines
- **Task-Specific Prompts**: Specialized instructions for summarization, analysis, and timeline reconstruction
- **Arabic Language Optimization**: Prompts designed for Arabic legal terminology
- **Structured Output**: Markdown-formatted responses with consistent structure

#### 9. **Google Drive OAuth2 Flow**
- **Local Server Receiver**: Dynamic port allocation (8080-8090) for OAuth callback
- **Token Storage**: File-based token persistence across application restarts
- **Automatic Authorization**: Browser-based OAuth consent flow
- **Credential Validation**: Client secret verification at startup

#### 10. **Virtual Threads Support**
- **Java 24 Virtual Threads**: Lightweight concurrency for high-throughput operations
- **Automatic Thread Pool**: Spring Boot auto-configuration for virtual threads

### Project Structure
```
lawyer/
├── src/main/java/com/mohamed/lawyer/
│   ├── auth/                    # Authentication & JWT handling
│   │   ├── AuthenticationController.java
│   │   ├── AuthenticationService.java
│   │   └── [DTOs: Request/Response]
│   │
│   ├── config/                  # Application configuration
│   │   ├── ApplicationConfig.java     # General beans
│   │   ├── SecurityConfiguration.java # Security setup
│   │   ├── JWTAuthenticationFilter.java
│   │   ├── JwtService.java
│   │   ├── LegalAIConfig.java         # AI system prompts
│   │   ├── GoogleDriveConfig.java
│   │   └── WebClientConfig.java
│   │
│   ├── handler/                 # Global exception handling
│   │   ├── GlobalExceptionHandler.java
│   │   └── ExceptionResponse.java
│   │
│   ├── lawsuit/                 # Core case management
│   │   ├── Lawsuit.java         # Entity
│   │   ├── LawsuitRepository.java
│   │   ├── LawsuitService.java  # Business logic
│   │   ├── LawsuitController.java
│   │   ├── LawsuitMapper.java
│   │   ├── Status.java          # Enum for case status
│   │   └── [DTOs: Request/Response]
│   │
│   ├── lawsuitdoc/              # Document management
│   │   ├── Doc.java             # Document entity
│   │   ├── DocRepository.java
│   │   ├── DocService.java      # Document operations & AI analysis
│   │   ├── DocController.java
│   │   ├── DocMapper.java
│   │   └── [DTOs: Request/Response/Analysis]
│   │
│   ├── lawyer/                  # Lawyer entity & repository
│   │   ├── Lawyer.java          # User entity (UserDetails)
│   │   └── LawyerRepository.java
│   │
│   ├── logs/                    # Case activity logging
│   │   ├── Logs.java            # Log entry entity
│   │   ├── LogsRepository.java
│   │   ├── LogsService.java     # Log management & notifications
│   │   ├── LogsController.java
│   │   ├── LogsMapper.java
│   │   └── [DTOs: Request/Response]
│   │
│   ├── storage/                 # Google Drive integration
│   │   ├── GoogleDriveService.java  # OAuth2 & file operations
│   │   ├── client_secret.json       # OAuth2 credentials (excluded from git)
│   │   └── tokens/                  # OAuth2 tokens (excluded from git)
│   │
│   ├── utils/                   # Utility classes
│   │   ├── ArabicNormalizer.java    # Arabic text processing
│   │   ├── FuzzyUtils.java          # Similarity algorithms
│   │   └── LegalPromptSpecifications.java  # AI prompt templates
│   │
│   ├── whatsapp/                # WhatsApp integration
│   │   ├── WhatsAppService.java     # Message delivery
│   │   └── [DTOs: Request/Response]
│   │
│   └── LawyerApplication.java   # Spring Boot main class
│
├── src/main/resources/
│   └── application.yml          # Application configuration
│
├── Dockerfile                   # Multi-stage Docker build
├── docker-compose.yml           # Service orchestration
├── .env                         # Environment variables (excluded from git)
├── pom.xml                      # Maven dependencies
└── DOCKER_OAUTH2_GUIDE.md       # OAuth2 setup documentation
```

### Key Engineering Solutions

#### 1. **Arabic Text Processing**
- **Normalization**: Remove diacritics (تشكيل) and normalize Arabic letters (أ، إ، آ → ا)
- **Fuzzy Search**: Levenshtein distance algorithm for handling spelling variations
- **Case-Insensitive Matching**: Proper handling of Arabic character encoding

#### 2. **Google Drive OAuth2 in Docker**
- **Challenge**: OAuth2 requires browser interaction, which is complex in containerized environments
- **Solution**:
  - Dynamic port allocation (8080-8090) for OAuth callback
  - Token persistence via Docker volumes
  - Support for pre-authorized tokens (copy from local development)
  - Detailed documentation for first-time setup

#### 3. **AI Model Privacy**
- **Challenge**: Legal documents contain sensitive information
- **Solution**:
  - Local Ollama deployment (no data leaves the infrastructure)
  - Lightweight model (Qwen 3 1.7B) for resource efficiency
  - Specialized prompt engineering for Egyptian legal context

#### 4. **Streaming AI Responses**
- **Challenge**: Large document analysis can take significant time
- **Solution**:
  - Reactive streams (Flux) for progressive response delivery
  - Server-Sent Events (SSE) for real-time client updates
  - Non-blocking architecture with WebFlux

#### 5. **Multi-Stage Docker Build**
- **Build Stage**: Maven with full JDK for compilation
- **Runtime Stage**: Minimal JRE for production deployment
- **Result**: Optimized image size and faster container startup

#### 6. **Virtual Threads**
- **Challenge**: High concurrency for document processing and AI analysis
- **Solution**: Java 24 virtual threads for lightweight, scalable concurrency

#### 7. **Comprehensive Legal Prompts**
- **Document Summarization**: Extract structured information (parties, dates, claims)
- **Legal Analysis**: Risk assessment, compliance, and strategic recommendations
- **Timeline Reconstruction**: Build case history with next-step suggestions
- All prompts designed specifically for Egyptian legal system and Arabic language

## Getting Started

### Prerequisites

- **Docker & Docker Compose**: For containerized deployment
- **Java 24**: For local development
- **Maven 3.9+**: For building the application
- **MySQL Database**: Hosted or local instance
- **Google Cloud Project**: With Drive API enabled
- **Ollama**: Installed locally with Qwen 3 model
- **WhatsApp Business API Access**: For client notifications

### Environment Configuration

1. **Create a `.env` file in the project root:**
   ```bash
   touch .env
   ```

2. **Configure the `.env` file with your settings:**
   ```properties
   # Database Configuration
   DATABASE_URL=jdbc:mysql://your-host:3306/lawyer_db?useSSL=true&serverTimezone=UTC
   DATABASE_USERNAME=your_username
   DATABASE_PASSWORD=your_password

   # JWT Configuration
   JWT_SECRET_KEY=your_secret_key_here
   JWT_EXPIRATION=86400000  # 24 hours in milliseconds

   # WhatsApp API Configuration
   WHATSAPP_API_KEY=your_api_key
   WHATSAPP_API_SECRET=your_api_secret
   WHATSAPP_API_URL=https://api.whatsapp.com/send
   WHATSAPP_FROM_NUMBER=your_business_number

   # Application Configuration
   APP_PORT=8080
   SPRING_PROFILE=prod
   HIBERNATE_DDL_AUTO=update
   SHOW_SQL=false
   ```

3. **Configure Google Drive OAuth2:**
   - Create a project in Google Cloud Console
   - Enable Google Drive API
   - Create OAuth2 credentials (Desktop application type)
   - Download `client_secret.json` and place it at:
     ```
     lawyer/src/main/java/com/mohamed/lawyer/storage/client_secret.json
     ```
   - See `DOCKER_OAUTH2_GUIDE.md` for detailed OAuth2 setup instructions

4. **Install and configure Ollama:**
   ```bash
   # Install Ollama
   curl -fsSL https://ollama.ai/install.sh | sh

   # Pull Qwen 3 model
   ollama pull qwen3:1.7b

   # Start Ollama service (default port: 11434)
   ollama serve
   ```

### Running the Application

#### Option 1: Docker Compose (Recommended)

1. **Build and start services:**
   ```bash
   docker-compose up --build
   ```

2. **For first-time OAuth2 authorization:**
   - Monitor logs for the OAuth URL:
     ```bash
     docker-compose logs -f app
     ```
   - Open the authorization URL in your browser
   - Complete the Google authentication flow
   - Tokens will be automatically saved

3. **Access the application:**
   - API Base URL: `http://localhost:8080/api/v1/`
   - Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

#### Option 2: Local Development

1. **Build the project:**
   ```bash
   cd lawyer
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Or run the JAR directly:**
   ```bash
   java -jar target/lawyer-0.0.1-SNAPSHOT.jar
   ```

### API Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register new lawyer account
- `POST /api/v1/auth/authenticate` - Login and get JWT token

#### Lawsuit Management
- `GET /api/v1/lawsuits` - Get all lawsuits
- `POST /api/v1/lawsuits` - Create new lawsuit
- `GET /api/v1/lawsuits/status/{status}` - Filter by status
- `GET /api/v1/lawsuits/client/{name}` - Search by client name
- `GET /api/v1/lawsuits/name/{name}` - Search by lawsuit name
- `PUT /api/v1/lawsuits/{id}/status` - Update lawsuit status
- `DELETE /api/v1/lawsuits/{id}` - Mark lawsuit as deleted

#### Document Management
- `POST /api/v1/docs/upload` - Upload document to lawsuit
- `GET /api/v1/docs/lawsuit/{lawsuitId}` - Get all documents for lawsuit
- `GET /api/v1/docs/{fileId}` - Download document
- `DELETE /api/v1/docs/{fileId}` - Delete document
- `POST /api/v1/docs/analyze/{fileId}` - AI-powered document analysis (streaming)
- `POST /api/v1/docs/summarize/{fileId}` - Generate document summary (streaming)

#### Case Logs
- `POST /api/v1/logs` - Add case log entry
- `GET /api/v1/logs/lawsuit/{lawsuitId}` - Get all logs for lawsuit
- `POST /api/v1/logs/analyze/{lawsuitId}` - Analyze case timeline with AI

### Testing

1. **Run unit tests:**
   ```bash
   mvn test
   ```

2. **Test with Swagger UI:**
   - Navigate to `http://localhost:8080/api/v1/swagger-ui.html`
   - Authenticate using the `/auth/authenticate` endpoint
   - Copy the JWT token
   - Click "Authorize" and paste the token
   - Test any endpoint interactively

### Monitoring & Logs

1. **View application logs:**
   ```bash
   docker-compose logs -f app
   ```

2. **View MySQL logs:**
   ```bash
   docker-compose logs -f mysql
   ```

3. **Check health status:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Troubleshooting

**Issue: OAuth2 authorization fails**
- Ensure ports 8080-8090 are accessible
- Verify `client_secret.json` is correctly placed
- Check redirect URIs in Google Cloud Console

**Issue: AI analysis not working**
- Verify Ollama is running: `curl http://localhost:11434`
- Check if Qwen model is pulled: `ollama list`
- Review application logs for AI-related errors

**Issue: WhatsApp notifications not sending**
- Verify WhatsApp API credentials in `.env`
- Check network connectivity to WhatsApp API
- Review WebClient logs for HTTP errors

**Issue: Database connection refused**
- Wait for MySQL container to be fully ready (health check)
- Verify database credentials in `.env`
- Check MySQL container status: `docker-compose ps`

**Issue: Port already in use**
- Change `APP_PORT` in `.env` file
- Or stop conflicting service: `lsof -ti:8080 | xargs kill -9`

### Additional Resources

- **Docker OAuth2 Guide**: See `DOCKER_OAUTH2_GUIDE.md` for detailed OAuth2 setup
- **API Documentation**: Access Swagger UI when application is running
- **Spring AI Documentation**: https://docs.spring.io/spring-ai/reference/
- **Ollama Documentation**: https://ollama.ai/docs

---

## Contact

For issues, questions, or contributions, please contact the project maintainer.

---

**Built with ❤️ for the Egyptian legal community**
