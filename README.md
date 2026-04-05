# Spring Security 핸즈온 (Kotlin)

Spring Security의 핵심 개념을 단계별로 학습하는 프로젝트입니다.

## 실행 방법

```bash
./gradlew bootRun
```

브라우저에서 http://localhost:8080 접속

### 테스트 계정
| 계정 | 비밀번호 | 역할 |
|------|----------|------|
| user | 1234 | USER (일반 사용자) |
| admin | 1234 | ADMIN (관리자) |

### H2 DB 콘솔
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:securitydb`
- User: `sa` / Password: (빈칸)

---

## 학습 가이드 (Step by Step)

### Step 1: 프로젝트 구조 파악

```
src/main/kotlin/com/example/security/
├── SpringSecurityHandsonApplication.kt  # 엔트리포인트
├── config/
│   ├── SecurityConfig.kt          # ★ Spring Security 핵심 설정
│   └── DataInitializer.kt         # 초기 데이터 (테스트 계정)
├── controller/
│   ├── HomeController.kt          # 페이지 컨트롤러
│   └── SignupController.kt        # 회원가입 컨트롤러
├── entity/
│   └── User.kt                    # 사용자 엔티티 + Role enum
├── repository/
│   └── UserRepository.kt          # JPA Repository
├── service/
│   ├── UserService.kt             # 사용자 비즈니스 로직
│   └── CustomUserDetailsService.kt # ★ Spring Security 인증 핵심
└── dto/
    └── SignupRequest.kt           # 회원가입 DTO
```

---

### Step 2: SecurityConfig 분석하기

`SecurityConfig.kt`을 열고 다음을 확인하세요:

**1. URL별 접근 권한**
```kotlin
.authorizeHttpRequests { auth ->
    auth
        .requestMatchers("/", "/signup").permitAll()     // 누구나 OK
        .requestMatchers("/admin/**").hasRole("ADMIN")   // ADMIN만
        .anyRequest().authenticated()                     // 나머지는 로그인 필요
}
```

**2. 폼 로그인 설정**
```kotlin
.formLogin { form ->
    form
        .loginPage("/login")           // 커스텀 로그인 페이지
        .defaultSuccessUrl("/", true)  // 성공 시 이동
        .failureUrl("/login?error")    // 실패 시 이동
}
```

**실습**: `permitAll()` → `authenticated()`로 바꿔보고 동작 차이를 확인하세요.

---

### Step 3: 인증 흐름 따라가기

Spring Security의 인증은 다음 순서로 진행됩니다:

```
1. 사용자가 /login 폼에서 username, password 입력
2. POST /login 요청 → Spring Security가 가로챔
3. CustomUserDetailsService.loadUserByUsername(username) 호출
4. DB에서 사용자 조회 → UserDetails 객체 반환
5. 입력된 password와 DB의 암호화된 password를 BCrypt로 비교
6. 일치 → Authentication 객체 생성 → SecurityContext에 저장
7. defaultSuccessUrl로 리다이렉트
```

**실습**: `CustomUserDetailsService.kt`에 로그를 추가해보세요:
```kotlin
override fun loadUserByUsername(username: String): UserDetails {
    println(">>> 로그인 시도: $username")  // 이 줄 추가
    // ...
}
```

---

### Step 4: 접근 제어 실험하기

1. **비로그인 상태**에서 `/mypage` 접속 → `/login`으로 리다이렉트 확인
2. **user**로 로그인 후 `/admin` 접속 → 403 Forbidden 확인
3. **admin**으로 로그인 후 `/admin` 접속 → 정상 접근 확인

**핵심**: `hasRole("ADMIN")`은 내부적으로 `ROLE_ADMIN` 권한을 확인합니다.

---

### Step 5: 회원가입 테스트

1. http://localhost:8080/signup 에서 새 계정 생성
2. H2 콘솔에서 비밀번호가 BCrypt로 암호화되었는지 확인:
   ```sql
   SELECT * FROM USERS;
   ```
3. 새 계정으로 로그인 → USER 권한 확인

---

### Step 6: 코드 수정 실험 (도전 과제)

아래 과제를 직접 구현해보세요:

1. **비밀번호 확인 필드 추가**: 회원가입 시 비밀번호 재입력 검증
2. **Remember Me 기능**: SecurityConfig에 `.rememberMe()` 추가
3. **로그인 실패 횟수 제한**: 5회 실패 시 계정 잠금
4. **403 에러 페이지 커스텀**: `AccessDeniedHandler` 구현
5. **REST API 인증**: 세션 대신 JWT 토큰 방식으로 변경

---

## 핵심 개념 요약

| 개념 | 설명 |
|------|------|
| `SecurityFilterChain` | 보안 필터 체인. 모든 HTTP 요청이 이 체인을 통과 |
| `UserDetailsService` | 사용자 정보를 로드하는 인터페이스. DB 조회 담당 |
| `PasswordEncoder` | 비밀번호 암호화/검증. BCrypt 사용 권장 |
| `Authentication` | 인증된 사용자 정보를 담는 객체 |
| `SecurityContext` | 현재 스레드의 Authentication을 보관하는 컨텍스트 |
| `Principal` | 현재 로그인한 사용자를 나타내는 인터페이스 |
| `hasRole()` | 역할 기반 접근 제어. 내부적으로 "ROLE_" 접두사 추가 |
| `CSRF` | Cross-Site Request Forgery 방어. POST 요청 시 토큰 필요 |

## 기술 스택

- Kotlin 1.9 + Spring Boot 3.4
- Spring Security 6.x
- Spring Data JPA + H2 (인메모리 DB)
- Thymeleaf (서버사이드 템플릿)
- Gradle (Kotlin DSL)
