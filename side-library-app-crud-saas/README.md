# Library App — a course-companion CRUD SaaS

A small but "real" full-stack app — Spring Boot REST API + React UI — built to sit
alongside Chad Darby's Spring Boot course in this repo. Every course lesson is a
5-minute toy example; this app puts the same ideas together into something with
actual shape: relationships, validation, business rules, pagination, error
handling. Reading it end to end should make the course lessons click faster,
because you see *why* each concept exists, not just *how* to write it.

It is deliberately small. No auth, no tests beyond "does it compile," no build
pipeline. Just enough surface area for a senior engineer to read in one sitting.

## The domain

A library: **Authors** write **Books**, **Members** borrow them via **Loans**.

```
Author 1───* Book 1───* Loan *───1 Member
```

- A `Book` has `totalCopies` and `availableCopies`. Checking out a book decrements
  `availableCopies`; returning it increments it back.
- A `Loan` is created with a 14-day due date. There's no scheduled job that flips
  loans to `OVERDUE` — the status is *computed on read* by comparing `dueDate` to
  today (see `LoanService.toDto`). That's a deliberate simplification worth
  noticing: it avoids a cron job / `@Scheduled` task for a status that's cheap to
  derive, but it does mean "overdue" is a UI concept, not a stored fact.
- Business rules that live in the service layer, not the database:
  - Can't check out a book with `availableCopies == 0`.
  - A member can't have more than 5 active loans at once.
  - Can't delete an `Author` who still has books, or a `Book`/`Member` with an
    open loan — the relationship has to be unwound first.

## Where things live

```
04-library-app-crud-saas/
├── backend/    Spring Boot 4.1 REST API (Java 21, Maven)
└── frontend/   React 19 + Vite SPA
```

### Backend — `backend/src/main/java/com/luv2code/libraryapp/`

| Package | Purpose |
|---|---|
| `entity/` | JPA entities — `Author`, `Book`, `Member`, `Loan`, `LoanStatus` |
| `repository/` | `JpaRepository` interfaces — mostly derived queries, no custom SQL |
| `service/` | Business logic, transactions, entity↔DTO mapping |
| `dto/` | Request/response shapes — never leak entities over the wire |
| `web/` | `@RestController`s — thin, delegate straight to services |
| `exception/` | Custom exceptions + `@RestControllerAdvice` for uniform error JSON |
| `config/` | `CorsConfig` — a `@Configuration` class with a `@Bean` method |

### Frontend — `frontend/src/`

| Path | Purpose |
|---|---|
| `api/client.js` | One `fetch` wrapper all pages share — parses errors into `Error` messages |
| `pages/` | One component per resource: `BooksPage`, `AuthorsPage`, `MembersPage`, `LoansPage` |
| `components/ErrorBanner.jsx` | The only shared UI component — intentionally minimal |
| `App.jsx` | Router + nav shell |

Each page owns its own `fetch`/`useState`/`useEffect` — there's no Redux, no
React Query, no generic `useResource` hook. For four pages that would be an
abstraction with one caller each. If you added a fifth or sixth resource,
that's the point where extracting a shared data-fetching hook would start
paying for itself.

## Suggested reading order

1. **`entity/Book.java` and `entity/Author.java`** — see the `@ManyToOne`/`@OneToMany`
   pair and read the comment on `Author.books` explaining why there's no cascade.
   This is the payoff of the course's JPA CRUD module
   ([03-jpa-crud-app/01-cruddemo](../03-jpa-crud-app/01-cruddemo)) applied to a
   real relationship instead of a single entity.
2. **`repository/BookRepository.java`** — a derived query method
   (`findByTitleContainingIgnoreCaseOrAuthor_LastNameContainingIgnoreCase`) that
   reaches across the relationship and returns a `Page`. Compare the method name
   to the SQL it generates (turn on `spring.jpa.show-sql=true` if you want to see it).
3. **`service/LoanService.java`** — the most interesting file. Constructor
   injection of three repositories, a transactional `checkout()` that enforces
   two business rules before touching the database, and the computed
   `OVERDUE` status.
4. **`exception/GlobalExceptionHandler.java`** — one place that turns
   `ResourceNotFoundException` → 404, `BusinessRuleException` → 409, and bean
   validation failures → 400, all with the same JSON shape. Trace a validation
   error from `dto/BookRequest.java`'s `@NotBlank` through here.
5. **`web/BookController.java`** — notice how little logic is in it. Controllers
   only translate HTTP ↔ service calls; if you're tempted to add an `if` here,
   it probably belongs in `BookService`.
6. **`frontend/src/pages/LoansPage.jsx`** — the checkout form loads books and
   members in parallel (`Promise.all`), disables already-exhausted books in the
   `<select>`, and re-fetches both loans and books after a checkout so available
   counts stay in sync.

## How the course concepts show up here

| Course lesson | Where it lives in this app |
|---|---|
| [02-01 constructor injection](../02-spring-boot-core/01-constructor-injection) | Every `@Service` and `@RestController` — e.g. `LoanService(LoanRepository, BookRepository, MemberRepository)`. No `@Autowired` needed with a single constructor. |
| [02-02 component scanning](../02-spring-boot-core/02-component-scanning) | `@SpringBootApplication` on `LibraryAppApplication` scans `com.luv2code.libraryapp` for `@Service`, `@Repository`, `@RestController`, `@Configuration`. |
| [02-03 setter injection](../02-spring-boot-core/03-setter-injection) | Not used here on purpose — the app is 100% constructor injection, which is what the course (and the Spring team) recommends once you've seen the alternative. |
| [02-04 qualifiers](../02-spring-boot-core/04-qualifiers) | Not needed — there's exactly one bean per interface. Qualifiers earn their keep with 2+ implementations, which this app doesn't have. |
| [02-05 primary](../02-spring-boot-core/05-primary) | Same reasoning as qualifiers — no competing beans to disambiguate. |
| [02-06 lazy initialization](../02-spring-boot-core/06-lazy-initialization) | Not enabled — a handful of beans in an app this size, so eager startup cost is negligible. |
| [02-07 bean scopes](../02-spring-boot-core/07-bean-scopes) | Every bean is the default `singleton` scope — one `BookService` instance serves every request. |
| [02-08 bean lifecycle methods](../02-spring-boot-core/08-bean-lifecycle-methods) | Not used, but `CorsConfig` is where you'd add `@PostConstruct`/`@PreDestroy` if a bean needed setup/teardown. |
| [02-09 java config bean](../02-spring-boot-core/09-java-config-bean) | `config/CorsConfig.java` — a `@Configuration` class producing a `@Bean` by hand, same pattern as the lesson. |
| [03-01 JPA CRUD](../03-jpa-crud-app/01-cruddemo) | `entity/`, `repository/` — extended from one entity to four, with real relationships, derived queries, and pagination instead of `findAll()`. |

## Running it

**1. Start MySQL** (this repo already has a container for it):

```bash
cd ../mysqldb
docker compose up -d
```

This reuses the existing `appdb` database / `appuser` credentials from
`mysqldb/compose.yaml` — no new database to create. On every backend restart,
the schema is dropped, recreated, and reseeded from
`backend/src/main/resources/data.sql` (`ddl-auto=create-drop`), so you always
start from the same known dataset while exploring.

**2. Start the backend** (port 8080):

```bash
cd backend
./mvnw spring-boot:run
```

**3. Start the frontend** (port 5173):

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The frontend calls the API directly at
`http://localhost:8080/api`; CORS for that origin is allowed in `CorsConfig`.

## API reference

| Method | Path | Notes |
|---|---|---|
| GET | `/api/authors` | List all |
| GET | `/api/authors/{id}` | |
| POST | `/api/authors` | |
| PUT | `/api/authors/{id}` | |
| DELETE | `/api/authors/{id}` | 409 if the author still has books |
| GET | `/api/books?query=&page=&size=` | Paginated, searches title or author last name |
| GET | `/api/books/{id}` | |
| POST | `/api/books` | |
| PUT | `/api/books/{id}` | 409 if lowering `totalCopies` below what's on loan |
| DELETE | `/api/books/{id}` | 409 if any copies are on loan |
| GET | `/api/members` | List all, with `activeLoanCount` |
| POST | `/api/members` | 409 on duplicate email |
| DELETE | `/api/members/{id}` | 409 if the member has active loans |
| GET | `/api/loans?activeOnly=true` | |
| POST | `/api/loans/checkout` | `{ bookId, memberId }` — 409 on no copies / loan limit |
| POST | `/api/loans/{id}/return` | |

Every error response has the same shape:

```json
{ "status": 409, "error": "Business Rule Violation", "message": "...", "timestamp": "..." }
```

Validation errors additionally include a `fieldErrors` map.
