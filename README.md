# TalentBoard

A full-stack recruitment management platform that allows companies to publish job vacancies, receive candidate applications, and manage the interview process — all through a clean web interface backed by a secure REST API.

---

## Table of Contents

- [Project Description](#project-description)
- [Technologies Used](#technologies-used)
- [Architecture Overview](#architecture-overview)
- [API Endpoints](#api-endpoints)
- [Environment Variables](#environment-variables)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Test Credentials](#test-credentials)
- [Seeded Data](#seeded-data)
- [API Documentation](#api-documentation)

---

## Project Description

TalentBoard is a recruitment board application built with a **Spring Boot** backend and a **React** frontend. It supports three user roles:

| Role | Capabilities |
|---|---|
| **Admin** | Full access: manage vacancies, applications, and interviews |
| **Recruiter** | Create and manage vacancies; review applications |
| **Candidate** | Browse open vacancies and submit applications |

Key features:
- JWT-based authentication and role-based access control
- Full CRUD for vacancies, applications, and interviews
- Status workflows (e.g., application goes from `PENDING` → `UNDER_REVIEW` → `INTERVIEW_SCHEDULED`)
- Automatic database seeding on every fresh startup (idempotent — no duplicate data on restart)
- OpenAPI/Swagger documentation available at runtime

---

## Technologies Used

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Primary language |
| Spring Boot | 4.0.7 | Application framework |
| Spring Security | (bundled) | Authentication & authorization |
| Spring Data JPA | (bundled) | Database ORM |
| PostgreSQL | 16 | Relational database |
| JWT (jjwt) | 0.11.5 | Token-based auth |
| Lombok | (bundled) | Boilerplate reduction |
| MapStruct | 1.5.5 | DTO ↔ Entity mapping |
| SpringDoc OpenAPI | 3.0.2 | Swagger UI |
| spring-dotenv | 4.0.0 | `.env` file support |
| Maven | (wrapper) | Build tool |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 18.3 | UI framework |
| Vite | 5.1 | Build tool & dev server |
| React Router DOM | 6.22 | Client-side routing |
| Axios | 1.6 | HTTP client |

### Infrastructure
| Tool | Purpose |
|---|---|
| Docker | Containerization |
| Docker Compose | Multi-service orchestration |

---

## Architecture Overview

```
┌─────────────────┐        HTTP/JSON        ┌──────────────────────┐
│   React Frontend │ ──────────────────────▶ │  Spring Boot Backend │
│   (port 3000)   │ ◀────────────────────── │     (port 8080)      │
└─────────────────┘                          └──────────┬───────────┘
                                                        │ JPA/JDBC
                                             ┌──────────▼───────────┐
                                             │   PostgreSQL 16       │
                                             │   (port 5443)         │
                                             └──────────────────────┘
```

---

## API Endpoints

### Authentication — `/api/users`
| Method | Path | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/users/register` | Register a new user | No |
| `POST` | `/api/users/login` | Login and receive JWT | No |

### Vacancies — Public
| Method | Path | Description | Auth required |
|---|---|---|---|
| `GET` | `/api/vacancies` | List all open vacancies | Yes |

### Vacancies — Admin
| Method | Path | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/admin/vacancies` | Create a vacancy | Yes (Admin) |
| `GET` | `/api/admin/vacancies` | List all vacancies | Yes (Admin) |
| `GET` | `/api/admin/vacancies/{id}` | Get vacancy by ID | Yes (Admin) |
| `PATCH` | `/api/admin/vacancies/{id}` | Update vacancy fields | Yes (Admin) |
| `PATCH` | `/api/admin/vacancies/{id}/status` | Change vacancy status | Yes (Admin) |

### Applications
| Method | Path | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/applications` | Submit an application | Yes |
| `GET` | `/api/applications/{id}` | Get application by ID | Yes |
| `GET` | `/api/applications/candidate/{candidateId}` | Get applications by candidate | Yes |
| `GET` | `/api/admin/applications` | List all applications | Yes (Admin) |
| `PATCH` | `/api/admin/applications/{id}/status` | Update application status | Yes (Admin) |

### Interviews — Admin
| Method | Path | Description | Auth required |
|---|---|---|---|
| `POST` | `/api/admin/interviews` | Schedule an interview | Yes (Admin) |
| `GET` | `/api/admin/interviews` | List all interviews | Yes (Admin) |
| `GET` | `/api/admin/interviews/{id}` | Get interview by ID | Yes (Admin) |
| `GET` | `/api/admin/interviews/application/{applicationId}` | Get interviews for an application | Yes (Admin) |
| `PATCH` | `/api/admin/interviews/{id}/status` | Update interview status | Yes (Admin) |

---

## Environment Variables

Create a `.env` file in the project root (same level as `docker-compose.yml`) with the following variables:

```env
# Database
DB_NAME=dbmanager
DB_USERNAME=dbmanager_user
DB_PASSWORD=CrudZaso2026!
DB_URL=jdbc:postgresql://postgres:5432/dbmanager

# JWT
JWT_SECRET=VMQtlKPTuGMo2w0ii7f2vv60Xhu6wMGHGzNjNnU8SEmQVJZx1EDoylSqEgQfoypr6j1bkqTHjoTmwx8JQbHZOKgF4I4WSf3CksFjvqQvSTonJCMzzk3M6V1AJ2fYv4WbeiHqWLqNBvorqAbRToWyoIT6dNAQP6t0z5yysR91ICToxubDqIPcHiJw2EDLPPOGmCl8vgTlWSXr7cbJKfSWoveqkJcuy4rPBgXmP9kPz1huoziFuuRIeuu71UYg4miB
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=86400000
```

> **Note:** `JWT_EXPIRATION` and `JWT_REFRESH_EXPIRATION` are in milliseconds. `86400000` = 24 hours.

---

## Installation

### Prerequisites

Make sure the following tools are installed on your machine:

- [Docker](https://docs.docker.com/get-docker/) 20+
- [Docker Compose](https://docs.docker.com/compose/install/) 2+

For local (non-Docker) development, you also need:

- [Java 21](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/download.cgi) (or use the included `./mvnw` wrapper)
- [Node.js 18+](https://nodejs.org/) and npm
- A running PostgreSQL 16 instance

---

## Running the Application

### Option 1 — Docker Compose (Recommended)

This is the easiest way to run the full stack on any machine.

**1. Clone the repository**
```bash
git clone <repository-url>
cd TalentBoard
```

**2. Make sure the `.env` file exists** in the project root (see [Environment Variables](#environment-variables)).

**3. Start all services**
```bash
docker compose up --build
```

This will start:
- **PostgreSQL** on port `5443`
- **Spring Boot backend** on port `8080`
- **React frontend** on port `3000`

**4. Open the app in your browser**
```
http://localhost:3000
```

**5. To stop the application**
```bash
docker compose down
```

To also remove the database volume (fresh start):
```bash
docker compose down -v
```

---

### Option 2 — Local Development (Without Docker)

#### Backend

**1. Set up a local PostgreSQL database** and create a user/database matching the values in your `.env`.

**2. Update the `DB_URL`** in your `.env` to point to your local database:
```env
DB_URL=jdbc:postgresql://localhost:5432/dbmanager
```

**3. Run the backend**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

#### Frontend

**1. Navigate to the frontend directory**
```bash
cd frontend
```

**2. Install dependencies**
```bash
npm install
```

**3. Start the development server**
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173` and will proxy API requests to `http://localhost:8080`.

---

## Test Credentials

The application automatically seeds the database with the following users on first startup:

| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@talentboard.com` | `Admin123*` |
| **Recruiter** | `recruiter@talentboard.com` | `Recruiter123*` |
| **Candidate** | `candidate@talentboard.com` | `Candidate123*` |

> These accounts are created only if the database is empty. Restarting the application will **not** create duplicate users.

---

## Seeded Data

In addition to the three users above, the following sample data is loaded automatically:

**5 Vacancies**
| Title | Category | Mode | Salary | Status |
|---|---|---|---|---|
| Senior Java Developer | Software Development | Remote | $85,000 | Open |
| Data Analyst | Data Analytics | Hybrid | $65,000 | Open |
| DevOps Engineer | DevOps | Remote | $90,000 | In Progress |
| UI/UX Designer | UI/UX Design | Onsite | $55,000 | Open |
| Cybersecurity Specialist | Cybersecurity | Hybrid | $95,000 | Open |

**3 Applications** (submitted by the candidate user)
- Senior Java Developer — `UNDER_REVIEW`
- Data Analyst — `PENDING`
- DevOps Engineer — `INTERVIEW_SCHEDULED`

**2 Interviews** (linked to the DevOps Engineer application)
- Virtual interview scheduled in 2 days — `SCHEDULED`
- In-person interview scheduled in 5 days — `SCHEDULED`

---

## API Documentation

Once the backend is running, interactive API documentation (Swagger UI) is available at:

```
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI JSON spec is at:
```
http://localhost:8080/v3/api-docs
```

---

## Evidence

> Add screenshots or screen recordings here to demonstrate the application working.
> Suggested evidence to include:
> - Login screen
> - Vacancy listing page
> - Application submission flow
> - Admin dashboard (applications and interviews)
> - Swagger UI showing available endpoints
