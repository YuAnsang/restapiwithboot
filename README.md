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
Lombok Annotation 추가
- @EqualsAndHashCode에서 of를 사용하는 이유 
    - (원하는 필드값만 비교)
- @Data를 쓰지 않는 이유 
    - (상호 참조에서 위험)
- Lombok Annotation은 줄여쓸 수 없다.

--- 

# Event 생성 API 구현 : 비즈니스 로직

- basePrice와 maxPrice 경우의 수와 각각의 로직
```
| basePrice | maxPrice |                                                                                                                           |
|-----------|----------|---------------------------------------------------------------------------------------------------------------------------|
| 0         | 100      | 선착순 등록                                                                                                               |
| 0         | 0        | 무료                                                                                                                      |
| 100       | 0        | 무제한 경매 (높은 금액을 지불한 사람이 등록)                                                                              |
| 100       | 200      | 제한가 선착순 등록 처음부터 200을 낸 사람은 선 등록. 100을 내고 등록할 수 있으나 더 많이 낸 사람에 의해 밀려 날 수 있음 |
```

#Event 생성 API 구현 : 테스트 생성

스프링 부트 슬라이스 테스트
- @WebMvcTest
    - MockMvc 빈을 자동 설정 해준다.
    - 웹 관련 빈만 등록해준다. (슬라이스)
    
MockMvc
- 스프링 MVC 테스트 핵심 클래스
- 웹 서버를 띄우지 않고도 스프링 MVC(DispatcherServlet)가 요청을 처리하는 과정을 확인 할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰인다.

테스트 할 것
- 입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인. 
    - Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인. 
    - id는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인 
- 입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면? 
    - Bad_Request로 응답 vs 받기로 받기로 한 값 이외는 이외는 무시무시 
- 입력 데이터가 이상한 경우 Bad_Request로 응답 
    - 입력값이 이상한 경우 에러 
    - 비즈니스 로직으로 검사할 수 있는 에러 
    - 에러 응답 메시지에 에러에 대한 정보가 있어야 한다. 
- 비즈니스 로직 적용 됐는지 응답 메시지 확인 
    - offline과 free 값 확인 
- 응답에 HATEOA와 profile 관련 링크가 있는지 확인. 
    - self (view) 
    - update (만든 사람은 수정할 수 있으니까) 
    - events (목록으로 가는 링크) 
- API 문서 만들기 
    - 요청 문서화 
    - 응답 문서화 
    - 링크 문서화 
    - profile 링크 추가
    
---
# Event 생성 API 구현 : 201 응답 받기
ReponseEntity을 사용하는 이유
- 응답 코드, 헤더, 본문 모두 다루기 편한 API

Location URI 만들기
- HATEOS가 제공하는 linkTo(), methodTo() 사용

객체를 JSON으로 변환
- ObjectMapper 사용

---
# Event 생성 API 구현 : EventRepository 구현

스프링 데이터 JPA
- JPARepository 상속받아 사용

Enum읗 JPA 맴핑시 주의 할 것
- @Enumerated (EnumType.STRING)

@MockBean
- Mockito를 사용해서 mock 객체를 만들고 빈으로 등록해줌
- (주의) 기존 빈을 테스트용 빈이 대체함.

테스트 할 것
```
입력값들을 전달하면 JSON 응답으로 201이 나오는지 확인
- Location 헤더에 생성된 이벤트를 조회할 수 있는 URI가 담겨 있는지 확인
- id는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인
```
---
# Event 생성 API 구현 : 입력값 제한하기

입력값 제한
- id 또는 입력받은 데이터로 계산해야 하는 값들은 입력받지 않아야 함.
- EventDto 적용

Dto -> 도메인 객체로 값 복사
```
compile group: 'org.modelmapper', name: 'modelmapper', version: '2.3.2'
```

테스트 할 것
- 입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면?
    - BadRequest로 응답 vs 받기로 한 값 이외는 무시
- Web쪽 관련된 테스트는 @SpringbootTest를 사용하는 것이 더 편함. 
    - 슬라이싱하면 mocking 할 것이 너무 많음
    - 관리도 힘듦 
---
# Event 생성 API 구현 : 입력값 이외에 에러 발생

ObjectMapper 커스터마이징
- spring.jackson.deserialization.fail-on-unknown-properties=true
- Bad_Request 발생

---

