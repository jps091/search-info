#  📑 Web Search with Naver Open API - 웹 검색 서비스 README

## 🚴 기능 요구사항
- 성능 최적화
  - 1년동안 100만건의 요청이 발생하는 서비스임을 고려해야 한다.
- 웹 검색 기능
  - 키워드로 검색을 시도하면 해당 키워드 관련 자료를 제공해야 한다.
- 웹 검색 키워드 통계 기능
  - 사용자들이 검색한 키워드의 횟수를 제공해야한다.
  - 사용자들이 검색한 키워드 순위 상위 15개를 제공해야한다.
 
---

## 📝 브랜치 목록
DEFAULT: dev
- NS-1: README.md 작성 
- NS-2: 요구사항 작성
- NS-3: 멀티 모듈 구성
- NS-4: 외부 api 연동
- NS-5: api 서버 구현
- NS-6: 검색 통계 기능 구현
- NS-7: 고가용성을 위한 설계(Circuit-Breaker) 
- NS-8: 고가용성을 위한 설계(@Async Thread Pool & Event Handle)
- NS-9: 쿼리 최적화

---

## 🗂 프로젝트 구조
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

## 🛠 기술 스택

- Backend: Java 17, Spring Boot 3.2.5, JDBC Template
- Database: PostgreSQL 15.1
- Build Tool: Gradle
- Version Control: Git, GitHub
- Infra: EC2, Nginx, SupaBase
- Containerization: Docker
- CI/CD: Github Actions, ECR, CodeDeploy
