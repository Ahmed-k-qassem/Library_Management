# Library Management System (LMS) API

A robust RESTful backend API built with Java and Spring Boot for managing library resources. This system handles the core operations for tracking books, authors, categories, and users, providing a solid foundation for library administration.

## Current Status: v1.0 (Development)

This initial release focuses on core CRUD functionalities and database relationships using Spring Data JPA.

**Security & Authentication:**
Currently, the application implements a raw/custom security model via `CustomAuthenticationProvider` and `SecurityConfig` to handle basic user authentication and endpoint protection.

## Roadmap: Upcoming in v2.0

*   **OAuth2 Integration:** Transitioning from the raw security model to robust OAuth2 authentication for enhanced security and standardized authorization flows.
*   **Comprehensive Logging:** Implementing system-wide logging to track API requests, error handling, and application state for better monitoring and debugging.

## Tech Stack

*   **Java**
*   **Spring Boot** (Web, Data JPA, Security)
*   **Database:** (Add your DB here, e.g., PostgreSQL / MySQL)
*   **Build Tool:** Maven

## Testing

All API endpoints have been rigorously tested and verified using **Postman** to ensure reliable routing, correct HTTP status codes, and accurate JSON payload processing.

## API Endpoints

Below is the structured list of available REST endpoints based on the current Postman testing environment.

### Authors (`/api/authors`)
Manage book authors and their details.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/authors` | Retrieve a list of all authors. |
| `GET` | `/api/authors/{id}` | Retrieve a specific author by their ID. |
| `POST` | `/api/authors` | Add a new author. |
| `PUT` | `/api/authors` | Update an existing author's full details. |
| `PATCH`| `/api/authors/{id}` | Partially update an author's details. |
| `DELETE`| `/api/authors/{id}` | Remove an author from the system. |

### Books (`/api/books`)
Manage the library's book inventory.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/books` | Retrieve a list of all books. |
| `GET` | `/api/books/{id}` | Retrieve a specific book by its ID. |
| `GET` | `/api/books/author/{id}` | Retrieve all books associated with a specific author ID. |
| `POST` | `/api/books` | Add a new book to the inventory. |
| `DELETE`| `/api/books/{id}` | Remove a book from the inventory. |

### Categories (`/api/categories`)
Manage genres and classifications for the books.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/categories` | Retrieve all categories. |
| `GET` | `/api/categories/{id}` | Retrieve a specific category by its ID. |
| `POST` | `/api/categories` | Create a new category. |
| `PUT` | `/api/categories/{id}` | Update an existing category. |
| `PATCH`| `/api/categories/{id}` | Partially update a category. |
| `DELETE`| `/api/categories/{id}` | Delete a category. |

### Users (`/api/users`)
Manage library patrons and system users.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/users` | Retrieve a list of all users. | *(Note: More user management endpoints to be expanded)* |

## Setup & Installation

1. Clone the repository:
   ```bash
   git clone [your-repo-link]