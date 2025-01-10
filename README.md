# 프로젝트
- Web Search with Naver Open API

# 기능 요구사항
- 뉴스 검색 기능
  - 키워드로 뉴스를 검색하여 해당 키워드 관련 뉴스를 제공해야 한다.
  - 검색 결과는 페이징 형태로 제공되어야 한다.
- 뉴스 키워드 통계 기능
  - 사용자들이 검색한 키워드의 횟수를 제공해야한다.
  - 사용자들이 검색한 키워드 순위 상위 5개를 제공해야한다.

# 브랜치 목록
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