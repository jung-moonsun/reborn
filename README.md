# Reborn Market 🛒

Spring Boot와 React 기반의 실시간 중고거래 플랫폼입니다.
JWT 인증, 카카오 소셜 로그인, 상품 CRUD, 실시간 채팅(WebSocket), 댓글, 이미지 업로드(S3) 등 실제 서비스에 가까운 백엔드 구조와 기능들을 직접 구현했습니다.

---

## 🔧 기술 스택

| 영역           | 사용 기술                                                         |
| ------------ | ------------------------------------------------------------- |
| **Backend**  | Spring Boot, Spring Security, JWT, OAuth2 (Kakao), JPA, MySQL |
| **Frontend** | React, React Router, Axios                                    |
| **Infra**    | AWS EC2, S3, Swagger, WebSocket                               |
| **기타**       | CI/CD, DTO/Entity 계층 분리 설계                                    |

---

## ✨ 핵심 기능

### 👤 회원

* JWT 기반 회원가입 / 로그인 / 로그아웃
* 카카오 소셜 로그인 (OAuth2)
* 마이페이지, 회원 정보 수정 및 탈퇴

### 📦 상품

* 상품 등록 / 수정 / 삭제
* 상품 목록 / 상세 조회
* 이미지 업로드 (AWS S3 연동)

### 💬 채팅

* WebSocket 기반 실시간 1:1 채팅
* 채팅방 생성 / 삭제
* 마지막 메시지 저장, 읽음 처리 기능

### 💬 댓글

* 댓글 / 대댓글 (트리 구조 기반)
* 댓글 작성 / 수정 / 삭제

### ⚙️ 인증 / 보안 / 예외 처리

* JWT 토큰 검증 및 인증 처리
* Spring Security 기반 권한 인가
* 전역(Global) 예외 처리 핸들러 적용

---

## 📚 API 명세서 (Swagger)

🔗 [Swagger UI 바로가기](http://reborn-app-env.eba-fdizev3m.ap-northeast-2.elasticbeanstalk.com/swagger-ui/index.html#/)

> ※ JWT가 필요한 API는 `Authorize` 버튼 클릭 후 토큰 입력 필요

---

## 🛠 실행 방법

```bash
# 백엔드
./gradlew build
java -jar build/libs/xxx.jar

# 프론트엔드
cd frontend
npm install
npm start
```

* Backend: [http://localhost:8080](http://localhost:8080)
* Frontend: [http://localhost:3000](http://localhost:3000)

---


## 🧩 ERD 및 설계 의도

![Untitled (1)](https://github.com/user-attachments/assets/4c749993-5a0a-4f31-b289-314b94e891a1)

### 주요 테이블 설계 설명

#### users

* 사용자 정보 테이블. 소셜 로그인 및 soft delete 구현.

#### products / product\_images

* 상품 정보 및 다중 이미지 관리용 테이블 분리 구조

#### comments

* 상품에 대한 댓글/대댓글을 위한 자기참조(parent\_id) 구조

#### chat\_rooms / chat\_messages

* 거래 단위의 1:1 채팅방 생성, 메시지 타입(enum)과 읽음 처리 포함

**설계 포인트 요약**:

* 실무 기준에 맞춘 정규화된 테이블 분리
* 소셜 로그인/이미지 업로드 등 확장 가능성 고려
* 채팅, 댓글, 상품의 연관관계를 명확히 표현함

---

## 📌 프로젝트 특징 요약

* **개인진행  프로젝트**
* 프론트엔드 + 백엔드 + 인프라 + 문서화까지 전 과정 직접 수행
* WebSocket, JWT, OAuth2 등 실무에서 사용하는 기술 다수 적용
* **도메인 기반 계층 분리**, 유지보수 가능한 백엔드 아키텍처 설계

---

## 📽 시연 영상

👉 [시연 영상 보러가기](https://www.youtube.com/watch?v=13NGo-gOYAk)

---

---

## 💡 구현 중 겪은 문제와 해결 방법

### 1. WebSocket 기반 실시간 채팅

**문제**: 구매자/판매자가 채팅방을 나갔다가 다시 들어오는 경우, 채팅방을 새로 만들지 않고 기존 방을 유지하는 로직이 복잡했습니다.  
**해결**: `exitedByBuyer`, `exitedBySeller` 플래그를 따로 관리하고, 채팅방 조회 시 조건 분기를 세분화하여 재사용 가능하도록 설계했습니다.

---

### 2. 댓글/대댓글 구조 설계

**문제**: 트리 구조를 가진 댓글(대댓글 포함)을 프론트에서 보기 좋은 구조로 반환하는 데 어려움이 있었습니다.  
**해결**: JPA의 자기참조 매핑(`parent_id`)을 통해 트리 구조를 구현하고, 서비스 계층에서 정렬/구조화를 통해 계층형 응답 형태로 반환했습니다.

---

### 3. AWS S3 이미지 업로드

**문제**: 이미지 업로드 중 예외 발생 시 클라이언트에서 원인을 파악하기 어려웠습니다.  
**해결**: 예외 상황에 맞는 응답 메시지를 커스터마이징하여 프론트에서 유저에게 정확한 피드백을 제공하도록 처리했습니다.

---

### 4. OAuth2(Kakao) 로그인 처리

**문제**: 소셜 로그인 시 동일한 이메일로 일반 로그인 사용자가 있을 경우 식별 충돌이 발생했습니다.  
**해결**: `provider`, `providerId` 컬럼을 별도 관리하여 로그인 방식에 따라 유저를 구분하고, 유효성 검사 로직을 개선했습니다.

---

## 🧠 마무리

개인 프로젝트를 진행하면서 서비스가 돌아가기 위해 어떤 구조가 필요한지 배우게 되었습니다.  
완벽하진 않지만 끝까지 책임지고 만들었다는 점에서 가장 큰 의미가 있는 프로젝트입니다.
