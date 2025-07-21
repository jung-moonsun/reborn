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

## ☁️ 배포 환경

- **백엔드**: AWS Elastic Beanstalk에 배포 완료  
- **DB**: AWS RDS(MySQL)  
- **이미지 저장소**: AWS S3  
- **API 문서**: Swagger UI 제공  

백엔드 서버는 AWS에 배포되어 있으며, Swagger를 통해 직접 API를 호출해볼 수 있습니다.  
프론트엔드는 로컬 환경에서 실행하여 배포된 백엔드와 연동됩니다.  

## 📚 API 명세서 (Swagger)

🔗 [Swagger UI 바로가기](http://reborn-app-env.eba-fdizev3m.ap-northeast-2.elasticbeanstalk.com/swagger-ui/index.html#/)

> ※ JWT가 필요한 API는 `Authorize` 버튼 클릭 후 토큰 입력 필요

---

## 🛠 로컬 실행 방법

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

**문제**: A와 B가 채팅 중일 때, B가 채팅방을 나가면 A는 기존 채팅방이 유지됩니다.
그 상태에서 A가 퇴장 사실을 모른 채 다시 메시지를 보내면

1.B 입장에서는 새로운 채팅방이 생성되고

2.A는 기존 채팅방이 그대로 유지되어야 하는 복잡한 로직이 필요했습니다.

**해결**: `exitedByBuyer`, `exitedBySeller` 플래그를 추가하여 퇴장 여부를 저장하고,
채팅방 조회 시 기존 방을 재사용할지 새로 생성할지 분기 처리했습니다.

이 방식으로 사용자 입장에서 자연스러운 채팅 경험을 제공했습니다.

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

## ✅ 예외 처리 설계

Reborn Market은 서비스 로직에서 **권한 및 유효성 검증 후 예외 처리**를 적용하여  
API 요청 시 발생할 수 있는 오류를 명확하게 전달하도록 설계했습니다.  

### 📌 글로벌 예외 처리

- `CustomException` + `ErrorCode` Enum으로 예외 유형을 관리  
- `@RestControllerAdvice` 기반 전역 예외 처리 핸들러 사용  
- 클라이언트가 **HTTP 상태코드 + 에러 메시지 + 에러 코드**를 명확히 받을 수 있도록 구현  

---

### 📌 예외 처리 패턴

1. **리소스 존재 여부 검증**
   - 없는 상품, 없는 사용자 요청 시 `orElseThrow()` 사용
2. **권한 검증**
   - 작성자가 아닌 사용자가 수정/삭제 시도 → `AccessDeniedException` 발생
3. **삭제된 사용자/상품 검증**
   - soft delete 된 경우 `UNAUTHORIZED_USER` 예외 처리

---

### 📌 예외 처리 예시

#### 1) 채팅방 생성 시 예외 처리

```java
// 상품이 없거나, 삭제된 사용자면 예외 발생
Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

User buyer = userRepository.findById(buyerId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

if (buyer.isDeleted()) {
    throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
}
```

PRODUCT_NOT_FOUND → 잘못된 상품 ID 요청 시

USER_NOT_FOUND → 존재하지 않는 사용자일 경우

UNAUTHORIZED_USER → 이미 탈퇴한 계정이 요청했을 경우

---

#### 2)  댓글 수정/삭제 권한 검증

```java
public void validateCommentOwner(Comment comment, Long userId) {
    if (!comment.getUser().getId().equals(userId)) {
        throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }
}
```

작성자가 아닌 사용자가 수정/삭제를 시도하면 UNAUTHORIZED_USER 반환

---

#### 3) 상품 수정 권한 검증

```java
@Transactional
public ProductResponse updateProduct(Long productId, ProductRequest request, Long userId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

    if (!product.getOwnerId().equals(userId)) {
        throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
    }

    product.update(request.getTitle(), request.getDescription(), request.getPrice());
    return ProductResponse.from(product);
}
```

상품이 없거나 작성자가 아닌 경우 예외 발생

@Transactional 로직 내에서 예외 발생 시 롤백 처리

--- 

### 📌 예외 응답 예시

```
{
  "success": false,
  "status": 400,
  "code": "PRODUCT_NOT_FOUND",
  "message": "상품이 존재하지 않습니다."
}
```

✅ 이런 패턴으로 모든 CRUD 요청과 인증/인가 흐름에 예외 처리를 적용했습니다.

---

## 🧠 마무리

개인 프로젝트를 진행하면서 서비스가 돌아가기 위해 어떤 구조가 필요한지 배우게 되었습니다.  
완벽하진 않지만 끝까지 책임지고 만들었다는 점에서 가장 큰 의미가 있는 프로젝트입니다.
