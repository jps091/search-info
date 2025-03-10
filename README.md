##  📑 Search-Info - 웹 검색, 채팅 제공 서비스(Kotlin)

### 📁 프로젝트 개요

Open API를 활용하여, 검색 서비스를 제공 및 인기 키워드를 주제로 단체 채팅 서비를 제공해 주는 플랫폼입니다. 

연간 1,000만 건의 요청이 발생하는 가상의 시나리오를 세우고, 이를 구체화한 정보를 토대로 쿼리 최적화, 인덱스, 테이블 파티셔닝을 통해 요구사항에 맞는 최적화 과정을 진행하였습니다. 

이전 프로젝트에서 RDS $20의 과금이 발생하였는데, SupaBase 같은 경우는 저장 공간 500MB까지 무료이기 때문에 해당 데이터베이스 서버를 선택하였습니다. 

또한 Nginx를 활용해 SSL/TLS 적용, 로드밸런서 역할 및 정적 파일을 클라이언트에 제공하여 WAS가 API 요청만 처리하도록 설계하였습니다.

---

### 📝 시나리오 구체화

<aside>

**1일 요청** → 10,000,000건 / 365일 = 27,400건

**1일 필요 저장 공간**(1건의 요청당 발생하는 데이터 최대 200Byte) → **5MB(인덱스 고려)**

**요구 TPS**(24시간 중 8시간 요청 발생) → 27,400건 / 28,800(8시간 초) = **2TPS(피크 타임 고려)**

</aside>

---

### 👥 기여도
프론트엔드(vue.js) 개발부터 백엔드, 배포 100% 기여

- [서비스 배포 링크](https://search-info.n-e.kr/#/)

- [프로젝트 관련 포스팅](https://github.com/jps091/search-info/wiki)

- [vue.js Github](https://github.com/jps091/search-info-client)
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

<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/d5c45f8e-8700-447a-969b-086e1c9d37c8" />

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
 
---

### 🚀 기능 구현 화면

  **홈 화면**

<img width="600" height="500" alt="image" src="https://github.com/user-attachments/assets/6dbcac11-f442-4678-bdbf-8dd0691dba7e" /></br></br>



  **검색 결과 화면**

<img width="600" height="500" alt="image" src="https://github.com/user-attachments/assets/84d8e2b0-f63f-483e-8246-d452576a64de" /></br></br>



  **채팅방 목록 화면**

<img width="600" height="500" alt="image" src="https://github.com/user-attachments/assets/01f9466a-fe1d-4c1f-8a15-3d96aaf04d46" /></br></br>


  **채팅방 상세 화면**

<img width="600" height="500" alt="image" src="https://github.com/user-attachments/assets/28eb3cbc-78c7-4d51-91a0-b1be812cf693" /></br></br>

---

### 📓 프로젝트 회고

지금까지 서비스를 개발하면서, 성능이 좋아진다고 생각되면 경험을 쌓기 위해 기술을 도입했었습니다. 

하지만 성능에 대한 추상적인 느낌만 존재하였습니다. 

1,000만 건의 요청이라는 숫자가 처음엔 막연하게 크게 느껴졌지만, 데이터를 기반으로 한 최적화 과정을 통해 막연했던 성능 개선의 감각을 구체적이고 실질적인 경험으로 전환할 수 있었습니다.

---

### 🛠 기술 스택

- Backend: Kotiln, Spring Boot 3.2.5, JDBC Template, JPA
- Database: PostgreSQL 15.1, Redis
- Build Tool: Gradle
- Version Control: Git, GitHub
- Infra: EC2, Nginx, SupaBase
- Containerization: Docker
- CI/CD: Github Actions, ECR, CodeDeploy, S3
