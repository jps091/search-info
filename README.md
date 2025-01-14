##  📑 Web Search with Naver Open API - 웹 검색 서비스(Kotlin)

### 🚴 기능 요구사항
- 성능 최적화
  - 1년동안 100만건의 요청이 발생하는 서비스임을 고려해야 한다.
- 웹 검색 기능
  - 키워드로 검색을 시도하면 해당 키워드 관련 자료를 제공해야 한다.
- 웹 검색 키워드 통계 기능
  - 사용자들이 검색한 키워드의 횟수를 제공해야한다.
  - 사용자들이 검색한 키워드 순위 상위 15개를 제공해야한다.
- Java에서 Kotlin으로 마이그레이션해야 한다.
 
---

### 📝 브랜치 목록
DEFAULT: dev
- KT-1: external 모듈 마이그레이션
- KT-2: common 모듈 마이그레이션
- KT-3: main api 모듈 마이그레이션
- 
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

<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/f80af4a1-cb8c-497b-bde0-d6b2785cdd7e" />

---

### 🛠 기술 스택

- Backend: Kotiln, Spring Boot 3.2.5, JDBC Template
- Database: PostgreSQL 15.1
- Build Tool: Gradle
- Version Control: Git, GitHub
- Infra: EC2, Nginx, SupaBase
- Containerization: Docker
- CI/CD: Github Actions, ECR, CodeDeploy
