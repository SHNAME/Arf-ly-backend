# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Arf-ly is a Spring Boot backend for a pet-care capstone project. It handles authentication, community posts, pet profiles, hospital search, push notifications, and medication reminders.

- **Java 17**, **Spring Boot 4.0.5**, **Gradle**
- **PostgreSQL** (port 5433 in local/dev), **Redis**, **AWS S3**, **Firebase FCM**, **Google Maps Places API**

## Build & Run Commands

```bash
# Build (skipping tests, as CI does)
./gradlew clean build -x test

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.capstone.arfly.SomeTest"

# Start local infrastructure (Postgres only)
docker-compose up -d

# Start prod infrastructure
docker-compose -f docker-compose.prod.yml up -d
```

### Required Environment Variables

The app loads configuration via environment variables. Create a `.env` file (loaded by `dotenv-java`) or export them directly:

| Variable | Purpose |
|---|---|
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | PostgreSQL credentials |
| `REDIS_HOST`, `REDIS_PORT` | Redis connection (defaults: localhost, 6379) |
| `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `S3_BUCKET_NAME` | S3 file storage |
| `ACCESS_SECRET`, `ACCESS_EXPIRATION`, `REFRESH_SECRET`, `REFRESH_EXPIRATION` | JWT tokens |
| `PASSWORD_RESET_SECRET`, `PASSWORD_RESET_EXPIRATION` | Password reset token |
| `GOOGLE_CLIENT_ID`, `GOOGLE_SECRET`, `GOOGLE_TYPE` | Google OAuth |
| `KAKAO_CLIENT_ID`, `KAKAO_SECRET` | Kakao OAuth |
| `NAVER_CLIENT_ID`, `NAVER_SECRET` | Naver OAuth |
| `TEST_USER_ID`, `TEST_USER_PASSWORD` | Test credentials |
| `firebase.key-path` | Path to Firebase service account JSON file |

Active profile is set via `spring.profiles.active` (`dev` or `prod`).

## Architecture

### Package Structure

Each domain is self-contained under `com.capstone.arfly.<domain>`:

```
arfly/
├── common/         # Cross-cutting concerns (auth, config, exceptions, S3, Firebase)
├── member/         # Registration, login, OAuth (Google/Kakao/Naver), JWT, terms
├── pet/            # Pet CRUD, breed/species/allergy data
├── community/      # Posts, comments, likes, search, mentions
├── hospital/       # Hospital search via Google Maps Places API
├── notification/   # FCM push notifications, medication reminders
├── diagnosis/      # Skin diagnosis reports and hospital recommendations
└── ad/             # Ad domain (domain models only)
```

Within each domain: `controller → service → repository → domain`, plus `dto/`, `event/`, `util/`.

### Authentication Flow

- `JwtTokenFilter` (`common/auth/`) extracts `Bearer` token from `Authorization` header, validates it against `ACCESS_SECRET_KEY`, and injects `Authentication` into `SecurityContextHolder`.
- Refresh tokens are stored in PostgreSQL (`RefreshToken` entity). Access token is short-lived; client uses `/auth/token/refresh` with a refresh token to rotate.
- OAuth providers (Google, Kakao, Naver) each have a dedicated `*Service` that exchanges the OAuth code for a profile, then delegates to `AuthService.createOauth()`.
- Firebase phone auth (`firebaseUid` + `phoneNumber`) is used for identity verification and phone-based account recovery.

Public endpoints (no JWT required) are declared in `SecurityConfig`:
`/auth/create`, `/auth/doLogin`, `/auth/google|kakao|naver/doLogin`, `/auth/refresh`, `/auth/token/refresh`, `/terms/latest`, `/member/check-username`, `/member/check-userId`, `/auth/phone/verify`, `/auth/id/find`, `/auth/password/verify`, `/auth/password/reset`, Swagger UI paths.

### Like System (Redis + Async Events)

Post likes use a two-layer approach to handle concurrency:

1. **Redis** (`post:like:<postId>` counter, `post:like:users:<postId>` set) handles real-time toggle and deduplication in `PostService.toggleLike()`.
2. **Spring Events** (`PostLikeEvent`) are published after each toggle and processed asynchronously (`@Async`) by `PostLikeEventHandler` after the transaction commits (`@TransactionalEventListener(AFTER_COMMIT)`), which writes `PostLike` rows to PostgreSQL.
3. **`LikeSyncScheduler`** runs every 5 minutes (initial delay 1 min) to SCAN Redis keys matching `post:like:[0-9]*` and batch-update the `like_count` column in the `Post` table via `PostRepository.batchUpdateLikeCount()`.

### File Upload (S3)

`S3Uploader` (common/util/) handles all file I/O:
- Images: max 50 MB; videos: max 100 MB per file, 100 MB per request.
- Supported image extensions: `jpg, jpeg, png, gif, bmp, webp, heic`; video: `mp4, mov, avi, mkv, webm`.
- Multi-file upload rolls back successfully uploaded S3 objects if any later upload fails.
- S3 URLs: signed (10-min expiry) for most content; public URLs for pet profiles and post images that need to stay accessible.
- `S3Scheduler` cleans up orphaned files (marked `IS_DELETED` in the `File` table) at midnight.

`PostWriter` is a separate `@Transactional` component that saves the post + image metadata together, keeping S3 upload outside the DB transaction (`PostService.createPost` uploads first, then calls `PostWriter`).

### Exception Handling

All domain exceptions extend `BusinessException`, which carries an `ErrorCode` enum (HTTP status + error code string + Korean message). `GlobalExceptionHandler` maps `BusinessException` to `ResponseEntity<ErrorResponse>` and `MethodArgumentNotValidException` to `400 VALIDATION_ERROR`.

### Scheduled Tasks

| Scheduler | Schedule | Purpose |
|---|---|---|
| `LikeSyncScheduler` | Every 5 min (initial 1 min) | Redis → DB like count sync |
| `S3Scheduler` | Daily at midnight | Delete orphaned S3 files |

### Notification

`FirebaseService` wraps `FirebaseMessaging` to send FCM push notifications. `NotificationService` persists `Notification` entities. `MedicationReminderService` manages pet medication alarm schedules.

### Comment Mentions

Mention format in comment body: `@[displayName](user:<id>)`. `PostService.validateContentMentions()` extracts all `user:<id>` values from the content with a regex and asserts they exactly match the `mentionedUserIds` set in the request DTO before saving.

## CI/CD

GitHub Actions (`.github/workflows/deploy.yml`) triggers on push to `main`:
1. Build with `./gradlew clean build -x test`
2. Build and push Docker image to Docker Hub
3. SSH into EC2 and deploy with `docker-compose.prod.yml`

## Code Style Rules

- **응답 언어**: Claude는 항상 **한국어**로 설명한다.
- **Javadoc**: 모든 public 메서드에 Javadoc 주석을 작성한다. 파라미터(`@param`), 반환값(`@return`), 예외(`@throws`)를 명시한다.
- **커밋 메시지**: 한글로 작성하며 다음 태그를 앞에 붙인다.
  - `[Feat]` 새 기능, `[Fix]` 버그 수정, `[Refactor]` 리팩터링, `[Docs]` 문서, `[Test]` 테스트, `[Chore]` 기타
  - 예시: `[Fix] 로그인 버그 수정`, `[Feat] 좋아요 Redis 캐싱 추가`
- **동시성 주의사항**: 코드를 작성하거나 리뷰할 때 아래 동시성 리스크를 함께 설명한다.
  - Redis `INCR`/`DECR`는 원자적이지만, Redis Set 존재 확인 → Set 추가의 두 단계는 원자적이지 않으므로 `SADD` 반환값으로 중복 여부를 판별하는 현재 패턴이 올바른지 확인한다.
  - `@Async` + `@TransactionalEventListener(AFTER_COMMIT)` 조합은 이벤트 발행 스레드와 처리 스레드가 달라 DB 커넥션 풀 고갈, 예외 전파 단절에 주의한다.
  - `@Scheduled` 배치(`LikeSyncScheduler`)가 여러 인스턴스에서 동시에 실행되면 중복 업데이트가 발생할 수 있으므로, 다중 인스턴스 배포 시 분산 락(Redisson 등) 또는 ShedLock 적용을 검토한다.
  - JPA `@Modifying` 쿼리 실행 전후 1차 캐시 동기화(`clearAutomatically = true`)를 고려한다.

## API Documentation

Swagger UI is available at `/swagger-ui/index.html` (springdoc-openapi 2.7.0). It is publicly accessible without JWT.
