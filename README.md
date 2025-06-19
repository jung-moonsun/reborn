# Reborn - 중고거래 플랫폼

Spring Boot와 React 기반의 실시간 중고거래 플랫폼입니다.

JWT 로그인, 카카오 소셜 로그인, 상품 등록/조회, 실시간 채팅(WebSocket), 댓글 기능, 이미지 업로드(S3) 등 실제 서비스에 가까운 백엔드 구조와 기능을 직접 구현했습니다.

---

## 🔧 기술 스택

| 영역       | 기술                                                            
| -------- | ------------------------------------------------------------- |
| Backend  | Spring Boot, Spring Security, JWT, OAuth2 (Kakao), JPA, MySQL |
| Infra    | AWS EC2, S3, Swagger, WebSocket                               |
| Frontend | React, React Router, Axios                                    |
| 기타       | CI/CD, DTO/Entity 계층 분리 구조                              |       

---

## 🧩 핵심 기능

### 👤 회원

* JWT 기반 회원가입 / 로그인 / 로그아웃
* 카카오 소셜 로그인 (OAuth2)
* 마이페이지, 회원 정보 수정 및 탈퇴

### 📦 상품

* 상품 등록 / 수정 / 삭제
* 상품 목록 / 상세 조회
* 이미지 업로드 (AWS S3 연동)

### 💬 채팅

* 실시간 채팅 (WebSocket)
* 채팅방 생성 / 삭제
* 마지막 메시지 저장, 읽음 처리

### 💬 댓글

* 상품 댓글 작성 / 수정 / 삭제
* 대댓글 (트리 구조 기반)

### ⚙️ 인증 / 예외 처리

* JWT 토큰 검증 및 인증 처리
* Spring Security 기반 인증/인가 체계
* 글로벌 예외 처리 핸들러 적용

---

## 📚 API 명세서 (Swagger)

🔗http://localhost:8080/swagger-ui/index.html

* JWT가 필요한 API는 Authorize 버튼을 통해 토큰 입력 후 테스트 가능

---

## 🛠 실행 방법

# 백엔드
./gradlew build

# 프론트엔드
cd frontend
npm start


* 백엔드: [http://localhost:8080](http://localhost:8080)
* 프론트엔드: [http://localhost:3000](http://localhost:3000)

---

## 📌 프로젝트 특징 요약

* 개인 진행 프로젝트
* 프론트 + 백엔드 + 배포 + 문서화
* WebSocket, JWT, OAuth2 등 실무에서 쓰이는 기술로 설계
* 계층 분리, 책임 분산, 유지보수 가능한 구조 설계
