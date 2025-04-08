##  📑 Search-Info - 웹 검색, 채팅 제공 서비스

### 📖 목 차
1. [프로젝트 개요](https://github.com/jps091/search-info?tab=readme-ov-file#%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B0%9C%EC%9A%94)
2. [프로젝트 기여도](https://github.com/jps091/search-info?tab=readme-ov-file#-%EA%B8%B0%EC%97%AC%EB%8F%84)
3. [시나리오 TPS 분석](https://github.com/jps091/search-info?tab=readme-ov-file#%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4-%EA%B5%AC%EC%B2%B4%ED%99%94)
4. [요구사항](https://github.com/jps091/search-info?tab=readme-ov-file#-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD)
5. [프로젝트 구조](https://github.com/jps091/search-info?tab=readme-ov-file#-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B5%AC%EC%A1%B0)
6. [브랜치 목록](https://github.com/jps091/search-info?tab=readme-ov-file#-%EB%B8%8C%EB%9E%9C%EC%B9%98-%EB%AA%A9%EB%A1%9D)
7. [기술 스택](https://github.com/jps091/search-info?tab=readme-ov-file#-%EA%B8%B0%EC%88%A0-%EC%8A%A4%ED%83%9D)
8. [시스템 아키텍쳐](https://github.com/jps091/search-info?tab=readme-ov-file#-aws-%EA%B8%B0%EB%B0%98-%EB%B0%B0%ED%8F%AC)
9. [데이터베이스 설계](https://github.com/jps091/search-info?tab=readme-ov-file#-erd)
10. [기능 구현 화면](https://github.com/jps091/search-info?tab=readme-ov-file#-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-%ED%99%94%EB%A9%B4)
11. [프로젝트 회고](https://github.com/jps091/search-info?tab=readme-ov-file#%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%9A%8C%EA%B3%A0)

---

### 📁 프로젝트 진행 배경

이번 프로젝트에서는 시나리오 기반 설계의 현실적인 한계를 경험하고, 불필요한 오버엔지니어링을 경계하며 실질적인 성능 최적화를 적용하는 것을 목표로 삼았습니다.

단순히 새로운 기술을 도입하는 것이 아니라, 1,000만 건의 요청을 처리해야 한다는 가상의 시나리오를 구체화하여 데이터 기반의 최적화 전략을 검증하고자 했습니다. 

또한, Open API 연동 과정에서 API 호출 비용을 고려한 설계 및 장애 대응 방안을 고민하며, 실무에서 발생할 수 있는 문제를 미리 대비하는 것을 중요하게 생각했습니다. 

더불어, 채팅 기능을 통해 웹소켓을 적용하고, HTTP·SSE 통신과의 차이점을 학습하며, 각각의 상황에 맞는 최적의 통신 프로토콜을 선택하는 경험을 쌓는 것을 목표로 하였습니다.

---

### 👥 기여도
**프론트엔드(Vue.js) 개발부터 백엔드, 배포까지 모든 과정 100% 기여**

- [서비스 배포 링크](https://search-info.n-e.kr/#/)

- [프로젝트 관련 포스팅](https://github.com/jps091/search-info/wiki)

- [Vue.js Github](https://github.com/jps091/search-info-client) : 클라이언트 코드를 빌드하여 Nginx로 서빙하고, 백엔드와 연결하여 배포 완료

---

### 📝 시나리오 구체화

<aside>

**1일 요청** → 10,000,000건 / 365일 = 27,400건

**1일 필요 저장 공간**(1건의 요청당 발생하는 데이터 최대 200Byte) → **5MB(인덱스 고려)**

**요구 TPS**(24시간 중 8시간 요청 발생) → 27,400건 / 28,800(8시간 초) = **2TPS(피크 타임 12:00 ~ 13:00)**

</aside>

---

### 🚴 요구사항

- 성능 최적화
  - 1년 동안 10,000만 건의 요청이 발생하는 서비스임을 고려해야 한다.
    
- 웹 검색 기능
  - 키워드로 검색을 시도하면 해당 키워드 관련 자료를 제공해야 한다.
  - Naver API 장애가 발생해도, 서비스가 정상 제공되어야 한다.

- 웹 검색 키워드 통계 기능
  - 사용자들이 검색한 키워드의 횟수를 실시간으로 반영해서 제공해야 한다.
  - 사용자들이 검색한 키워드 순위 월간 상위 10개를 제공해야 한다.

- 단체 채팅방
  - 익명성 채팅방이지만, 사용자를 식별해야 한다.
  - 채팅방 인원 관리를 해야 한다.
  - 채팅 접속 시 현재 참여 인원 목록을 제공해야 한다.
  - 같은 사용자가 동일한 채팅방에 접속을 여러 번해 도 이를 식별해야 한다.
 
---

### 🗂 프로젝트 구조
**1. 멀티 모듈 구조 COMMON, NEWS-API, EXTERNAL(NAVER, KAKAO)**

```markdown
search
├── common # 공통 모듈
├── external # 외부 API 모듈
│   └── naver-api # 네이버 API 서버 모듈
│   └── kakao-api # 카카오 API 서버 모듈
└── news-api # 검색 서비스 모듈
```

**2. 관련 모듈 의존성 파일**
- common Gradle 파일은 최상위 Gradle 설정을 그대로 가져가기 때문에 존재하지 않음

<img width="800" height="500" alt="image" src="https://github.com/user-attachments/assets/40e4da0f-e0c5-4557-8072-1d45ae185c8c" />

---

### 📝 브랜치 목록
DEFAULT: dev
- KT-1: external 모듈 마이그레이션
- KT-2: common 모듈 마이그레이션
- KT-3: main api 모듈 마이그레이션
- KT-4: Naver API KEY 관리 기능 추가
- KT-5: Executro Thread Pool Custom & Load Test Script 추가
- KT-6: 화면 UI 변경 및 기능 수정
- KT-7: CICD 구축 workflow 작성
- KT-8: LogBack 활용하여 슬랙에 알람보내기 XML 설정
- KT-9: 정적 파일 삭제 with NGINX
- KT-10: 의존성 추가, profile 기능 활성화, 디렉토리 구조 변경
- KT-11: 실시간 검색순위 업데이트를 기존 폴링 방식에서 SSE 도입
- KT-12: 서버 증설을 대비한 Redis 기반 키관리 (Profile, 인터페이스를 활용하여 상황에 맞는 의존성 주입)
- KT-13: 단체 채팅방 API 구현
- KT-14: 채팅 메시지 관련 API 
- KT-15: 다중 서버 환경에서 브로드캐스팅을 위한 Redis Pub/Sub 구현
- KT-16: 12, 15 브랜치에 구현한 기능을 Redisson 기반으로 리팩토링
- KT-17: 테스트코드 재구성
---

### 🗂 프로젝트 구조
- 멀티 모듈 구조 COMMON, NEWS-API, EXTERNAL(NAVER, KAKAO) 

```markdown
search
├── common # 공통 모듈
├── external # 외부 API 모듈
│   └── naver-api # 네이버 API 서버 모듈
│   └── kakao-api # 카카오 API 서버 모듈
└── news-api # 검색 서비스 모듈
```
---

### 🛠 기술 스택

- Backend: Kotiln, Spring Boot, JDBC Template, JPA, Stomp, Resilience4j
- Frontend: Vue.js
- Test: JUnit5, Spock
- Database: PostgreSQL, Redis, Caffeine
- Load Test: JMeter, PostMan
- Infra: EC2, Nginx, SupaBase
- Containerization: Docker
- CI/CD: Github Actions, ECR, CodeDeploy, S3

---

### ☁ AWS 기반 배포

<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/94a33cc7-75a0-4e61-928c-005b420ce5a6" />
  
---

### 💼 데이터베이스 설계

**해당 프로젝트의 테이블 관계는 비교적 단순합니다. 하지만 몇 가지 테이블은 테이블 파티셔닝을 고려하여 외래키 대신 단순 참조 형식으로 설계되었습니다.**

- search 테이블: search_keyword 필드는 원래 chat_rooms 테이블의 외래키로 설정되어야 하지만, 파티셔닝을 적용하기 위해 단순 참조 방식으로 관리됩니다.
- chat_rooms 및 chat_messages 테이블: chat_room_id 역시 외래키로 설정하는 대신, 단순 참조 형식으로 설계되었습니다.

<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/79e10cb3-61ee-4fa7-910f-0167e3ed04f4" />

---

### 🚀 기능 구현 화면

<img width="900" height="500" alt="image" src="https://github.com/user-attachments/assets/717808c6-0802-47c8-8600-121f81af1393" /></br>

<img width="900" height="500" alt="image" src="https://github.com/user-attachments/assets/f6c9b80c-2ae0-457c-bd9f-5be46df838de" />

---

### 📓 프로젝트 회고

이번 프로젝트를 진행하면서 Nginx를 리버스 프록시로 사용하여 로드밸런싱, SSL/TLS 적용, 정적 파일 서빙, IP 기반 요청수 제한 등 다양한 기능을 구현하며 Nginx 웹 서버에 대한 이해도를 크게 향상시킬 수 있었습니다.

또한, 성능 최적화에 대해 처음엔 막연하게 "빠르게" 해야 한다는 생각을 가지고 있었으나, 1,000만 건의 요청을 처리하는 과정에서 데이터 기반의 최적화를 통해 성능 개선의 감각을 구체적이고 실질적인 경험으로 전환할 수 있었습니다.

Open API 연동에서는 API 호출 비용을 최적화하고 장애 대응을 고려한 설계를 통해 실무에서 발생할 수 있는 상황들을 경험할 수 있엇고, 웹소켓을 활용한 실시간 채팅 기능을 구현하면서 통신 프로토콜의 차이와 Stomp 프로토콜 사용법을 학습하여 실제 서비스를 구축하는 데 있어 중요한 기술적 통찰을 얻었습니다.

마지막으로, Vue.js를 통한 프론트엔드 개발 과정에서 axios 기반의 API 통신을 깊이 있게 학습하며, 프론트엔드와 백엔드 간의 원활한 데이터 흐름을 구현하는 데 필요한 기술적 이해를 높일 수 있었습니다.
