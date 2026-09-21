# Enterprise RFP Response Copilot

An AI-powered backend application for automating responses to Request for Proposal (RFP) questions using **Retrieval-Augmented Generation (RAG)**.

The application ingests RFP documents, extracts vendor-response questions using an LLM, stores the questions in PostgreSQL, retrieves relevant information from an enterprise knowledge base using **BM25 + semantic vector search**, combines the results using **Reciprocal Rank Fusion (RRF)**, and generates grounded answers using an LLM.

It also stores the generated answers and their status back against the corresponding RFP questions.

---

## Features

- RFP document upload using PDF/DOCX
- Text extraction using Apache Tika
- Automatic RFP question extraction using an LLM
- PostgreSQL persistence for RFPs and questions
- Knowledge document ingestion
- Text chunking
- Local BGE-small-en-v1.5 embeddings
- Elasticsearch vector storage
- BM25 keyword search
- Semantic vector search
- Hybrid retrieval using RRF
- Context construction from retrieved evidence
- Grounded RAG answer generation
- `SUPPORTED` / `INSUFFICIENT_EVIDENCE` response status
- Automatic generation of answers for all questions belonging to an RFP
- Generated answers persisted in PostgreSQL
- Dockerized Elasticsearch

---

## Architecture

```text
                         ┌──────────────────────┐
                         │      RFP Document    │
                         │      PDF / DOCX      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     Apache Tika      │
                         │    Text Extraction   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   LLM Question      │
                         │     Extraction      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                              PostgreSQL
                         ┌──────────────────────┐
                         │ RFPs + RFP Questions │
                         └──────────────────────┘


                          Knowledge Documents
                                 │
                                 ▼
                          ┌──────────────────────┐
                          │ Apache Tika / Text   │
                          │ Extraction           │
                          └──────────┬───────────┘
                                     ▼
                          ┌──────────────────────┐
                          │ Text Chunking        │
                          └──────────┬───────────┘
                                     ▼
                          ┌──────────────────────┐
                          │ BGE-small-en-v1.5    │
                          │ Local Embeddings     │
                          └──────────┬───────────┘
                                     ▼
                                Elasticsearch
                             ┌───────────────────┐
                             │ Text + Embeddings │
                             │ + Metadata        │
                             └───────────────────┘
                    
                    
                      RFP Question
                           │
                           ▼
                      ┌───────────────┐      ┌──────────────────┐
                      │ BM25 Search   │      │ Vector Search    │
                      └───────┬───────┘      └────────┬─────────┘
                              │                       │
                              └───────────┬───────────┘
                                          ▼
                                   RRF Hybrid Search
                                          │
                                          ▼
                                   Top Relevant Chunks
                                          │
                                          ▼
                                   Context Builder
                                          │
                                          ▼
                                       LLM / RAG
                                          │
                                          ▼
                                   Grounded Answer
                                          │
                                          ▼
                                    PostgreSQL
```

---

## RAG Pipeline

For every RFP question:

```text
RFP Question
     ↓
BM25 Search ──────┐
                  ├── RRF Fusion
Vector Search ────┘
     ↓
Top Relevant Evidence
     ↓
Context Builder
     ↓
Grounded Prompt
     ↓
LLM
     ↓
RagAnswer
     ↓
Store Answer Against RFP Question
```

### Hybrid Retrieval

The application does not directly add BM25 and vector scores because they use different score scales.

Instead, it uses **Reciprocal Rank Fusion (RRF)**:

```text
RRF Score = 1 / (k + rank)
```

with:

```text
k = 60
```

Documents appearing highly in both BM25 and semantic search therefore receive a stronger combined ranking.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Data JPA | PostgreSQL persistence |
| PostgreSQL | RFP/question/application data |
| Elasticsearch 9.1.4 | Search and vector storage |
| Apache Tika 3.2.3 | PDF/DOCX text extraction |
| LangChain4j | LLM and AI application integration |
| OpenRouter | LLM access |
| BAAI BGE-small-en-v1.5 | Local text embeddings |
| ONNX Runtime | Local embedding inference |
| Docker / Docker Compose | Elasticsearch infrastructure |
| Maven | Build and dependency management |

---

# API Documentation

The following APIs are based on the current controller implementations.

## Health API

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Checks whether the application/service is running |

The endpoint delegates to `HealthService` and returns the health-check response. fileciteturn5file0

---

## RFP APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/rfps/create` | Upload an RFP document, create the RFP record, extract questions, and store the questions |
| POST | `/api/rfps/generateresponse?id={id}` | Generate RAG-based answers for all questions belonging to the specified RFP |
| GET | `/api/rfps/getAnswers?id={id}` | Retrieve the questions and generated answers for the specified RFP |

### Create RFP

**Endpoint**

```http
POST /api/rfps/create
Content-Type: multipart/form-data
```

**Form fields**

```text
title
customerName
document
```

Example:

```bash
curl -X POST http://localhost:8080/api/rfps/create \
  -F "title=Enterprise Cybersecurity Framework" \
  -F "customerName=Apex Enterprise Technologies" \
  -F "document=@sample-rfp.pdf"
```

The uploaded RFP is processed, its text is extracted, questions are identified using the LLM, and the questions are stored in PostgreSQL. fileciteturn5file2

### Generate Responses

```http
POST /api/rfps/generateresponse?id=1
```

This loads all questions belonging to RFP `1`, sends each question through the RAG pipeline, and stores the generated answer and status against the corresponding question. fileciteturn5file2

### Get Generated Answers

```http
GET /api/rfps/getAnswers?id=1
```

Returns the questions and their generated answers for the specified RFP. fileciteturn5file2

---

## Knowledge Document API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/knowledgeDocument/register` | Upload and register a knowledge document for the RAG knowledge base |

**Request**

```http
POST /knowledgeDocument/register
Content-Type: multipart/form-data
```

Fields:

```text
docName
document
```

Example:

```bash
curl -X POST http://localhost:8080/knowledgeDocument/register \
  -F "docName=NIST CSF 2.0" \
  -F "document=@NIST-CSF-2.0.pdf"
```

The document is passed to the knowledge-document service for registration and processing. fileciteturn5file1

---

# Development / Testing APIs

The `/test` endpoints were created for development and verification of individual components. They are useful while developing the application but should not be exposed as production APIs.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/test/docTika?rfpId={id}` | Extracts and displays text from an RFP using Apache Tika |
| GET | `/test/test-llm` | Tests the LLM connection |
| GET | `/test/test-qextract?rfpId={id}` | Tests extraction of questions from an RFP |
| GET | `/test/embedding?text={text}` | Generates an embedding for supplied text |
| GET | `/test/indexing` | Indexes a sample security-policy document into Elasticsearch |
| POST | `/test/docTika/document` | Uploads a document and tests Apache Tika extraction |
| GET | `/test/search/bm25?query={query}` | Performs BM25 keyword search |
| GET | `/test/search/vector?query={query}` | Performs semantic vector search |
| GET | `/test/search/getHybridResult?question={question}` | Executes the RAG answer pipeline |
| GET | `/test/context?query={query}` | Builds context from hybrid-search results |
| POST | `/test/rag?question={question}` | Generates a grounded RAG answer |

These endpoints correspond to the current `TestController` implementation. fileciteturn5file3

---

# Project Structure

A simplified structure of the application:

```text
src/main/java/com/rfpcopilot/rfp_response_copilot
│
├── controller
│   ├── HealthController
│   ├── KnowledgeDocumentController
│   ├── RfpController
│   └── TestController
│
├── service
│   ├── RfpService
│   ├── RfpResponseService
│   ├── DocumentExtractionService
│   ├── QuestionExtractionService
│   └── rag
│       ├── EmbeddingService
│       ├── KnowledgeIndexingService
│       ├── KnowledgeSearchService
│       ├── HybridSearchService
│       ├── ContextBuilderService
│       └── RagService
│
├── model
│   ├── Rfp
│   ├── RfpQuestion
│   └── KnowledgeChunk
│
├── repository
│   ├── RfpRepository
│   └── RfpQuestionsRepository
│
└── dto
    ├── CreateRfpRequest
    ├── ExtractedQuestion
    ├── ExtractedQuestions
    ├── SearchResult
    └── RagAnswer
```

---

# How to Run

## Prerequisites

Install:

- Java 21
- Maven 3.9+
- PostgreSQL
- Docker Desktop
- Git

You also need an **OpenRouter API key** for the LLM functionality.

The embedding model runs locally, so embeddings do not require an external embedding API.

---

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <YOUR_REPOSITORY_DIRECTORY>
```

---

## 2. Configure PostgreSQL

Create a PostgreSQL database for the application.

For example:

```sql
CREATE DATABASE rfp_copilot;
```

Configure your Spring Boot datasource in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rfp_copilot
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Use your own PostgreSQL username and password.

---

## 3. Configure OpenRouter

Set the environment variable:

### Windows PowerShell

```powershell
$env:OPENROUTER_API_KEY="your-api-key"
```

### Windows permanent environment variable

```powershell
setx OPENROUTER_API_KEY "your-api-key"
```

Restart the terminal after using `setx`.

The application reads the key through:

```java
System.getenv("OPENROUTER_API_KEY")
```

Do **not** commit the API key to GitHub.

---

## 4. Start Elasticsearch

The project uses Elasticsearch through Docker Compose.

Example `docker-compose.yml`:

```yaml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:9.1.4
    container_name: rfp-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms1g -Xmx1g"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

volumes:
  elasticsearch_data:
```

Start it:

```bash
docker compose up -d
```

Verify Elasticsearch:

```text
http://localhost:9200
```

The application connects to:

```properties
spring.elasticsearch.uris=http://localhost:9200
```

---

## 5. Build the Application

Run:

```bash
mvn clean install
```

---

## 6. Start Spring Boot

Run:

```bash
mvn spring-boot:run
```

The application will normally be available at:

```text
http://localhost:8080
```

Check:

```http
GET http://localhost:8080/api/health
```

---

# Typical Workflow

## Step 1 — Register Knowledge Documents

Upload company policies, security documents, product documentation, or other information that the RFP Copilot should use as its knowledge base.

```http
POST /knowledgeDocument/register
```

The document is processed and indexed into Elasticsearch.

---

## Step 2 — Upload an RFP

```http
POST /api/rfps/create
```

The system:

1. Stores the RFP metadata.
2. Stores the uploaded document.
3. Extracts text using Apache Tika.
4. Sends the extracted text to the LLM.
5. Identifies vendor-response questions.
6. Stores the questions in PostgreSQL.

---

## Step 3 — Generate Responses

```http
POST /api/rfps/generateresponse?id=1
```

For each RFP question:

```text
Question
   ↓
BM25 + Vector Search
   ↓
RRF
   ↓
Top relevant knowledge chunks
   ↓
Context Builder
   ↓
LLM
   ↓
Grounded Answer
   ↓
PostgreSQL
```

---

## Step 4 — Retrieve Generated Answers

```http
GET /api/rfps/getAnswers?id=1
```

This returns the questions and their generated answers.

---

# Grounding and Insufficient Evidence

The RAG prompt instructs the LLM to use only the retrieved evidence.

If the knowledge base does not contain sufficient information, the system returns:

```text
INSUFFICIENT_EVIDENCE
```

instead of encouraging the model to invent an answer.

For example:

```text
Question:
Does the vendor need to be ISO 27001 certified?

Result:
INSUFFICIENT_EVIDENCE
```

This is important for enterprise RFP workflows where unsupported claims can create compliance and contractual risks.

---

# Embeddings

The project uses:

**BAAI/bge-small-en-v1.5**

The model generates **384-dimensional embeddings** locally.

The embedding pipeline is:

```text
Knowledge Document
      ↓
Text Chunk
      ↓
BGE-small-en-v1.5
      ↓
384-dimensional vector
      ↓
Elasticsearch
```

The embedding model runs locally rather than calling a paid embedding API.

---

# Search Strategy

The application uses two retrieval strategies.

### BM25

Useful for exact terminology, keywords, product names, policy names, and domain-specific terms.

### Vector Search

Useful for semantic similarity where the question and evidence may use different wording.

### Hybrid Search

The two result sets are combined using RRF:

```text
BM25 Results
     +
Vector Results
     ↓
RRF
     ↓
Hybrid Ranking
```

This allows the application to benefit from both lexical and semantic retrieval.

---

# Example

A question such as:

```text
How is data protected at rest?
```

may retrieve evidence such as:

```text
"Our company uses AES-256 encryption
to protect customer data at rest."
```

The LLM then generates a grounded response:

```text
Data at rest is protected using AES-256 encryption.
```

with a supported status.

---

# Security Considerations

- Never commit `OPENROUTER_API_KEY`.
- Keep secrets in environment variables or a secure secret manager.
- `/test` endpoints should be disabled or protected in production.
- Uploaded documents should be validated before processing.
- File storage should use controlled directories and access policies.
- Retrieved enterprise knowledge should be protected from unauthorized access.
- LLM-generated responses should be treated as grounded suggestions and reviewed according to organizational requirements.

---

# Current Limitations

- Local file storage is used for uploaded documents.
- The current RAG implementation is focused on text-based documents.
- OCR for scanned/image-only documents is not currently part of the pipeline.
- The current implementation uses a simple text chunking strategy.
- The application currently uses a single knowledge-retrieval pipeline.
- Authentication and authorization are not yet implemented.
- The `/test` endpoints are intended for development and debugging.

---

# Future Improvements

Possible future enhancements include:

- Better semantic/structure-aware document chunking
- OCR for scanned documents
- RAG evaluation metrics
- Retrieval quality evaluation
- Persistent citation records
- Streaming LLM responses
- Authentication and role-based access control
- Production-grade document storage
- Background/asynchronous document processing
- AI agent with tool/function calling
- RFP response review and approval workflow
- Response versioning
- Human feedback and answer evaluation
- Production observability and metrics

---

# Author

**MDF**

Built as an enterprise-oriented AI/RAG project to explore:

- Spring Boot
- Enterprise document processing
- Elasticsearch
- Hybrid retrieval
- RAG
- LLM integration
- Local embeddings
- AI-assisted RFP response generation
