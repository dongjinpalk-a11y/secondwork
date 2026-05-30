# Spring Boot 4 + Spring Security 7 실습

Spring Boot 4 / Spring Security 7을 이용한 Session 기반 인증 실습 프로젝트입니다.

## 기술 스택

| 항목 | 버전 |
|------|------|
| Spring Boot | 4.0.6 |
| Spring Security | 7.x |
| Java | 21 |
| MySQL | 9.0 (Docker) |
| Thymeleaf | 4.x |
| JPA / Hibernate | 7.x |
| Lombok | 최신 |
| Bootstrap | 5.3.3 |

## 프로젝트 구조

```
src/main/java/kr/ac/hansung/
├── config/
│   └── SecurityConfig.java            ← Security 핵심 설정 (URL 레벨 접근 제어)
├── controller/
│   ├── AuthController.java            ← 로그인 폼 + 회원가입
│   ├── HomeController.java            ← 홈 페이지
│   ├── AdminController.java           ← 관리자 대시보드 (통계)
│   └── ProductController.java         ← 상품 목록/등록/삭제
├── dto/
│   ├── UserDto.java
│   └── ProductDto.java
├── entity/
│   ├── User.java
│   ├── Role.java
│   └── Product.java
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   └── ProductRepository.java
├── service/
│   ├── CustomUserDetailsService.java  ← UserDetailsService 구현
│   ├── UserService.java
│   └── ProductService.java
├── DataInitializer.java               ← 초기 데이터 생성 (관리자 계정 + 샘플 상품)
└── HelloSpringSecurityApplication.java
```

## 실행 방법

### 1. MySQL Docker 실행

```bash
docker compose up -d
```

### 2. 애플리케이션 실행

```bash
mvn spring-boot:run
```

또는 IDE에서 `HelloSpringSecurityApplication.java` 메인 클래스를 실행합니다.

### 3. 접속 URL

| URL | 설명 | 권한 |
|-----|------|------|
| http://localhost:8080/login | 로그인 페이지 | 누구나 |
| http://localhost:8080/signup | 회원가입 페이지 | 누구나 |
| http://localhost:8080/home | 홈 | 로그인 필요 |
| http://localhost:8080/products | 상품 목록 | 로그인 필요 |
| http://localhost:8080/products/add | 상품 등록 | ADMIN |
| http://localhost:8080/admin/dashboard | 관리자 대시보드 | ADMIN |

### 4. 초기 계정

앱 시작 시 `DataInitializer`가 자동으로 생성합니다.

| 이메일 | 비밀번호 | 권한 |
|--------|----------|------|
| admin@hansung.ac.kr | admin1234 | ROLE_ADMIN, ROLE_USER |

### 5. Docker 종료

```bash
docker compose down        # 컨테이너 종료 (데이터 유지)
docker compose down -v     # 컨테이너 + 볼륨 삭제 (데이터 초기화)
```

## 테스트 실행

```bash
mvn test
```

테스트는 H2 인메모리 DB를 사용하므로 Docker 없이 실행됩니다 (`src/test/resources/application.properties`).

| 테스트 클래스 | 테스트 수 | 설명 |
|---|---|---|
| `AuthControllerTest` | 5개 | 로그인/회원가입 흐름, CSRF |
| `ProductControllerTest` | 7개 | 인증/인가 기반 상품 접근 제어 |
| `HelloSpringSecurityApplicationTests` | 1개 | 컨텍스트 로드 확인 |

## 핵심 개념

### 인증 흐름

```
POST /login (email + password)
  → UsernamePasswordAuthenticationFilter
  → DaoAuthenticationProvider
  → CustomUserDetailsService.loadUserByUsername()
  → BCryptPasswordEncoder.matches()
  → 성공 시 세션 생성 + JSESSIONID 쿠키 발급
```

### 접근 제어 (SecurityConfig — URL 레벨 통합 관리)

| URL 패턴 | 권한 |
|---|---|
| `/`, `/login`, `/signup`, 정적 리소스 | 누구나 |
| `/admin/**` | ROLE_ADMIN |
| `/products/add`, `/products/*/delete` | ROLE_ADMIN |
| `POST /products` | ROLE_ADMIN |
| 그 외 모든 요청 | 로그인 사용자 |

### 관리자 대시보드 통계

`AdminController`에서 Repository를 직접 조회하여 아래 통계를 화면에 전달합니다.

- 전체 회원 수
- 전체 상품 수
- 재고 없는 상품 수 (stock = 0)
