# 1:1 실시간 채팅 시스템

Spring Boot + React 기반의 AI 필터링 기능이 포함된 실시간 1:1 채팅 애플리케이션입니다.

## 📋 프로젝트 개요

이 프로젝트는 사용자 간 1:1 실시간 채팅을 제공하며, Ollama AI를 활용하여 메시지를 자동으로 필터링하고 정중한 문체로 변환합니다. 또한 특정 문맥을 감지하여 티켓을 자동 생성하는 기능을 포함합니다.

## 🚀 주요 기능

### 1. 실시간 1:1 채팅
- **WebSocket(STOMP)** 기반 실시간 양방향 통신
- 사용자 간 1:1 채팅방 자동 생성 및 관리
- 실시간 메시지 전송 및 수신

### 2. AI 메시지 필터링 (Ollama)
- **욕설 및 부적절한 표현 자동 제거**
- **정중하고 존중하는 문체로 자동 변환**
- 원문 메시지는 절대 저장하지 않음 (메모리에서도 즉시 제거)
- 필터링된 메시지만 상대방에게 전달 및 DB 저장

### 3. 티켓 자동 생성
- AI가 특정 문맥을 감지하면 자동으로 티켓 생성
- 감지 문맥 예시:
  - "티켓을 생성한다", "티켓 생성해줘", "티켓 등록"
  - "이건 티켓으로", "티켓 만들어줘" 등
- 티켓 미리보기 메시지로 채팅방에 표시
- 미리보기 클릭 시 티켓 상세 정보 확인 가능

### 4. 읽음 표시
- 메시지 읽음/안읽음 상태 관리
- 실시간 읽음 상태 업데이트

### 5. 메시지 자동 만료
- 메시지 보관 기간: **90일**
- 매일 새벽 2시 자동 삭제 (스케줄러)
- 만료된 메시지는 DB에서 완전 삭제

## 🤖 Ollama의 역할

### Ollama가 수행하는 작업

1. **메시지 필터링**
   - 사용자가 보낸 원본 메시지를 분석
   - 욕설, 비속어, 부적절한 표현 제거
   - 정중하고 존중하는 문체로 변환
   - 필터링된 메시지만 반환

2. **티켓 생성 트리거 감지**
   - 메시지 내용에서 티켓 생성 의도를 감지
   - "티켓 생성", "티켓 등록" 등의 문맥 인식
   - 티켓 생성 필요 여부를 boolean 값으로 반환

3. **스트리밍 응답**
   - 실시간 스트리밍으로 응답 생성
   - 서버 로그에서 생성 과정 확인 가능
   - `[Ollama 스트리밍]` 로그로 각 토큰 확인

### Ollama 설정
- **모델**: `qwen3:8b`
- **엔드포인트**: `http://localhost:11434/api/chat`
- **응답 형식**: JSON
- **스트리밍**: 활성화

### Ollama 프롬프트 구조
```
시스템 프롬프트:
- 메시지 필터링 및 티켓 생성 판단 AI 역할
- 욕설 제거 + 정중한 문체 변환
- 티켓 생성 트리거 문맥 감지

응답 형식:
{
  "filteredMessage": "필터링된 메시지",
  "shouldCreateTicket": true/false
}
```

## 🏗️ 기술 스택

### Backend
- **Spring Boot 3.1.4**
- **Spring WebSocket (STOMP)**: 실시간 통신
- **Spring Data JPA**: 데이터베이스 연동
- **MariaDB**: 관계형 데이터베이스
- **Ollama**: AI 모델 (qwen3:8b)
- **Java 21**

### Frontend
- **React 18**
- **SockJS + STOMP.js**: WebSocket 클라이언트
- **Axios**: REST API 호출
- **Tailwind CSS**: 스타일링

## 📊 데이터베이스 구조

### 주요 테이블

1. **chat_room**: 1:1 채팅방 정보
2. **chat_message**: AI 필터링된 메시지 (원문은 저장 안 함)
3. **chat_read**: 메시지 읽음 상태
4. **ticket**: 자동 생성된 티켓 정보

## 🔄 메시지 처리 흐름

```
1. 사용자 A가 메시지 전송
   ↓
2. 서버가 Ollama에 원본 메시지 전달
   ↓
3. Ollama가 메시지 필터링 및 티켓 생성 여부 판단
   ↓
4. 서버가 필터링된 메시지만 수신
   ↓
5. 원문 메시지는 메모리에서 즉시 제거
   ↓
6. 티켓 생성 필요 시 Ticket 엔티티 생성
   ↓
7. 필터링된 메시지 + 티켓 미리보기(필요시) DB 저장
   ↓
8. 사용자 B에게 실시간 전송 (WebSocket)
```

## 🚦 실행 방법

### 사전 요구사항
- Java 21
- Node.js 18+
- MariaDB
- Ollama (qwen3:8b 모델 설치 필요)

### Backend 실행
```bash
cd desk
./gradlew bootRun
```

### Frontend 실행
```bash
cd deskfront
npm install
npm start
```

### Ollama 실행
```bash
# Ollama 서버 시작
ollama serve

# 모델 다운로드 (별도 터미널)
ollama pull qwen3:8b
```

## ⚙️ 설정

### application.properties
```properties
# 서버 포트
server.port=8080

# 데이터베이스 설정
spring.datasource.url=jdbc:mariadb://localhost:3306/aisdb
spring.datasource.username=aisdbuser
spring.datasource.password=aisdbuser

# Ollama 설정
ollama.url=http://localhost:11434
ollama.model=qwen3:8b
```

## 📝 주요 API

### WebSocket
- **연결**: `ws://localhost:8080/ws`
- **메시지 전송**: `/app/chat.send`
- **메시지 수신**: `/topic/chat/{chatRoomId}`

### REST API
- `GET /api/chat/rooms/{chatRoomId}/messages?userId={userId}`: 메시지 목록 조회
- `POST /api/chat/messages/{messageId}/read?userId={userId}`: 읽음 처리
- `GET /api/tickets/{ticketId}`: 티켓 정보 조회

## 🔒 보안 및 개인정보 보호

- **원문 메시지 보호**: 원문은 절대 저장하지 않으며, 메모리에서도 즉시 제거
- **CORS 설정**: 개발 환경에서는 모든 origin 허용 (운영 환경에서는 특정 도메인 지정 필요)
- **메시지 만료**: 90일 후 자동 삭제로 데이터 보호

## 📌 주요 특징

✅ **원문 메시지 미저장**: 사용자 프라이버시 보호  
✅ **실시간 AI 필터링**: Ollama 스트리밍으로 즉시 처리  
✅ **자동 티켓 생성**: 문맥 인식 기반 자동화  
✅ **읽음 표시**: 실시간 읽음 상태 관리  
✅ **자동 만료 처리**: 90일 후 자동 삭제  

---

**개발 환경**: Spring Boot 3.1.4, React 18, Ollama (qwen3:8b)
