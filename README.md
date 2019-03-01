REST API

**API**
- Application Programming Interface의 약자


**REST**
- REpresentational State Transfer
- 인터넷 상의 시스템 간의 상호 운용성을 제공받는 방법 중 하나
- 시스템 제각각의 독리벅인 진화를 보장하기 위한 방법
- REST API : REST 아키텍처 스타일을 따르는 API

**REST 아키텍쳐 스타일**
- Client - Server
- Stateless
- Cache
- Uniform Interface
- Layered System
- Code On Demand (Optional)

**Uniform Interface**
- Identification of resources
- manipulation of resources through represenations
- **self-descrive messages**
- **hypermisa as the engine of application state (HATEOAS)**

오늘날에 REST API라고 불리는 것의 대부분이 아래 2가지를 지키고 있지 않음
두 문제를 좀 더 자세히 살펴보자
- Self-descriptive message
    - 메시지 스스로 메시지에 대한 설명이 가능해야함
    - 서버가 변해서 메시지가 변해도 클라인터는 그 메시지를 보고 해석이 가능
    - **확장 가능한 커뮤니케이션**
- HATEOAS
    - 하이퍼미디어(링크)를 통해 애플리케이션 상태 변화가 가능해야한다.
    - 링크 정보를 동적으로 바꿀 수 있다. (Versioning 할 필요 없이)
    

Self-descriptive message 해결 방법!
- 방법 1 : 미디어타입을 정의하고 IANA에 등록 하고 그 미디어 타입용 리소스를 리턴할 때 Content-Type으로 이용한다.
- **방법 2 : profile 링크 헤더를 추가한다**
    - 브라우저들이 아직 스팩지원을 잘 안함.
    - 대안으로 HAL의 링크 데이터에 profile 링크 추가
    
HATEOAS 해결 방법
- 방법 1 : 데이터에 링크 제공
    - 링크를 어떻게 정의할 것인가? HAL
- 방법 2 : 링크 헤더나 Location 제공

---

# Event REST API

이벤트 등록 조회 및 수정 API

GET /api/events

이벤트 목록 조회 REST API (로그인 하지 않은 상태)
- 응답에 보여줘야 할 데이터
    - 이벤트 목록
    - 링크
        - self
        - profile : 이벤트 목록 조회 API 문서로 링크
        - get-an-event : 이벤트 하나 조회하는 API 링크
        - next : 다음 페이지 (optional)
        - prev : 이전 페이지 (optional)

- 문서
    - 스프링 REST docs로 만듦


이벤트 목록 조회 REST API (로그인 한 상태)
- 응답에 보여줘야 할 데이터
    - 이벤트 목록
    - 링크
        - self
        - profile : 이벤트 목록 조회 API 문서로 링크
        - get-an-event : 이벤트 하나 조회하는 API 링크
        - **create-new-event : 이벤트를 생성 할 수 있는 API 링크**
        - next : 다음 페이지 (optional)
        - prev : 이전 페이지 (optional)

- 로그인 한 상태???? (stateless라며...)
    - 아니, 사실은 Bearer 헤더에 유효한 AcecessToken이 들어있는 경우!
    
    
POST /api/evnets
- 이벤트 생성

GET /api/events/{id}
- 이벤트 하나 조회

PUT /api/evnets/{id}
- 이벤트 수정

---

# 프로젝트 생성

의존성 추가
- Web
- JPA
- HATEOAS
- REST Docs
- H2
- PostgreSQL
- Lombok

스프링부트 핵심 원리
- 의존성 설정
- 자동 설정(@EnableAutoConfiguration)
- 내장 웹 서버
- 독립 실행 가능한 JAR

--- 

