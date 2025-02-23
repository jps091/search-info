##  📑 Search-Info with OpenAPI, Stomp - 웹 검색, 채팅 제공 서비스(Kotlin)

### 🚴 기능 요구사항
- 성능 최적화
  - 1년동안 10,000만건의 요청이 발생하는 서비스임을 고려해야 한다.
- 웹 검색 기능
  - 키워드로 검색을 시도하면 해당 키워드 관련 자료를 제공해야 한다.
- 웹 검색 키워드 통계 기능
  - 사용자들이 검색한 키워드의 횟수를 제공해야한다.
  - 사용자들이 검색한 키워드 순위 월간 상위 10개를 제공해야한다.
- 단체 채팅방
  - 익명성 채팅방이지만, 사용자를 식별해야한다.
  - 채팅방 인원 관리를 해야한다.
  - 채팅 접속시 현재 참여 인원 목록을 제공해야한다.
 
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

### 🛠 기술 스택

- Backend: Kotiln, Spring Boot 3.2.5, JDBC Template, JPA
- Database: PostgreSQL 15.1, Redis
- Build Tool: Gradle
- Version Control: Git, GitHub
- Infra: EC2, Nginx, SupaBase
- Containerization: Docker
- CI/CD: Github Actions, ECR, CodeDeploy, S3
