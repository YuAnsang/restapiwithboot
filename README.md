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

# Event 생성 API 구현 : Bad Request 처리하기

@Valid와 BindingResult (또는 Errors)
- BindingResult는 항상 @Valid 바로 다음 인자로 사용해야함. (스프링 MVC)
- @NotNUll, @NotEmpty, @Min, @Max ... 사용해서 입력 값 바인딩 시 에러 확인

도메인 Validator 만들기
- Validator 인터페이스 만들기
- 없이 만들어도 상관없음

 테스트 할 것
```
 입력 데이터가 이상한 경우 Bad_Request로 응답
     - 입력값이 이상한 경우 에러
     - 비즈니스 로직으로 검사할 수 있는 에러
     - 에러 응답 메시지에 에러에 대한 정보가 있어야한다.
```
---

# Event 생성 API 구현 : Bad Request 응답 본문 만들기

커스텀 JSON Serializer 만들기
- extends JsonSerializer<T> (Jackson JSON 제공)
- @JsonComponent (스프링 부트 제공)

BindingError
- FieldError와 GlobalError (ObjectError)가 있음
- objectName
- defaultMessage
- code
- field
- rejectedValue
---

# 스프링 HATEOAS 소개

스프링 HATEOAS
- 주소 : [HATEOAS](https://docs.spring.io/spring-hateoas/docs/current/reference/html/)
- 링크 만드는 기능
    - 문자열 가지고 만들기
    - 컨트롤러와 메서드로 만들기 
- 리소스 만드는 기능
    - 리소스 : 데이터 + 기능
- 링크 찾아주는 기능
    - Traverson
    - LinkDiscoverers
- 링크 (2가지 정보)
    - HREF 
    - REL (Relation - 현재  리소스와의 관계)
        - self (자기 자신)
        - profile (응답 본문에 대한 문서)
        - update-event (이벤트 수정)
        - query-events (이벤트 조회)
---

# 스프링 HATEOAS 적용

EventResource 만들기
    - extends ResourceSupport의 문제 (composition 필드들은 serialize 시 필드명을 따라가게 되어있음)
        - @JsonUnwrapped로 해결
        - extends Resource<T>로 해결
    
테스트 할 것
```
응답에 HATEOAS와 profile 관련 링크가 있는지 확인
- self (view)
- update (수정 기능)
- events (목록 링크)
```

# 스프링 REST Docs 소개

[https://docs.spring.io/spring-restdocs/docs/2.0.2.RELEASE/reference/html5/](https://docs.spring.io/spring-restdocs/docs/2.0.2.RELEASE/reference/html5/)

snippet(문서 조각)을 사용하여 HTML 코드등으로 변환해준다.

REST Docs 자동 실행
- @AutoConfigureRestDoc (Test 위에)

REST Docs 코딩
- andDo(document("문서명"), snippets))
- snippets
    - links()
    - requestParameters() + parameterWithName() 
    - pathParameters() + parametersWithName() 
    - requestParts() + partWithname() 
    - requestPartBody() 
    - requestPartFields() 
    - requestHeaders() + headerWithName() 
    - requestFields() + fieldWithPath() 
    - responseHeaders() + headerWithName() 
    - responseFields() + fieldWithPath() 
- Relaxed
- Processor
    - preprocessRequest(prettyPrint()) 
    - preprocessResponse(prettyPrint()) 
Constraint 

---

# 스프링 REST Docs 적용

REST Docs 자동실행
- @AutoConfigureRestDocs

RestDocMockMvc 커스터마이징
- RestDocsMockMvcCongifurationCustomizer 구현한 빈 등록
- @TestConfiguration

테스트 할 것
```
API 문서 만들기
- 요청 본문 문서화  (o)
- 응답 본문 문서화  (o)
- 링크 문서화
    - Profile 링크 추가
- 응답 헤더 문서화
```

---

# 스프링 REST Docs - 링크, (Req, Res) 필드와 헤더 문서화

요청 필드 문서화
- requestFields() + fieldWithPath()
- responseFields() + fieldWithPath()
- requestHeaders() + headerWithName() 
- responseHeaders() + headerWithName()
- links() + linkWithRel()

테스트 할 것
```
API 문서 만들기
- 요청 본문 문서화  (o)
- 응답 본문 문서화  (o)
- 링크 문서화
    - self
    - query-events
    - update-event
    - Profile 링크 추가
- 응답 헤더 문서화
- 요청 헤더 문서화
- 요청 필드 문서화
- 응답 헤더 문서화
- 응답 필드 문서화
```

Relaxed 접두어
- 장점 : 문서 일부분만 테스트 할 수 있다.
- 단점 : 정확한 문서를 생성하지 못한다.
---

# 스프링 REST Docs: 문서 빌드

---

# PostgreSQL 적용

테스트 할 때는 계속 H2를 사용해도 조ㅗㅎ지만 어플리케이션 서버를 실행 할 때는 PostgreSQL을 사용하도록 변경(Docker 사용)

1. PostgreSQL 드라이버 의존성 추가
2. 도커로 PostgreSQL 컨테이너 실행
```
docker run --name ndb -p 5432:5432 -e POSTGRES_PASSWORD=pass -d postgres
```
3. 도커 컨테이너에 들어가보기
```
docker exec -it ndb bash
su - postgres
psql -d postgres -U postgres
\l
\dt
```
4. 데이터소스 설정
```
spring.datasource.username=postgres
spring.datasource.password=pass
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.driver-class-name=org.postgres.Driver
```
5. 하이버네이트 설정
```
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

애플리케이션 설정과 테스트 설정 중복을 어떻게 줄일 것인가?
- 프로파일과 @ActiveProfiles 활용

application-test.properties
```
application-test.properties
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.hikari.jdbc-url=jdbc:h2:mem:testdb
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```
---
# API 인덱스 만들기

인덱스 만들기
- 다른 리소스에 대한 링크 제공
- 문서화

```
@GetMapping("/api")
public ResourceSupport root() {
    ResourceSupport index = new ResourceSupport();
    index.add(linkTo(EventController.class).withRel("events"));
    return index; 
}
```

테스트 컨트롤러 리펙토링

에러 리소스
- 인덱스로 가는 링크 제공
---

# 이벤트 목록 조회 API 구현

페이징, 정렬은???
- 스프링 데이터 JPA가 제공하는 Pageable

Page<Event>안에 들어있는 Event들은 리소스로 어떻게 변경?
- 하나씩 순회하면서 직접 EventResource로 매핑
- PagedResourceAssembler<T> 사용

테스트 할 때 Pageable 파라미터 제공하는 방법
- page : 0부터 시작
- size : 기본값 20
- sort : property,property(ASC|DESC)
---

# 이벤트 수정 API

테스트 할 것
```
수정하려는 이벤트가 없는 경우 404 NOT_FOUND 입력 데이터
(데이터 바인딩)가 이상한 경우에 400 BAD_REQUEST 도메인
로직으로 데이터 검증 실패하면 400 BAD_REQUEST 
(권한이 충분하지 않은 경우에 403 FORBIDDEN) 정상적으로 수정한 경우에
이벤트 리소스 응답
    ● 200 OK
    ● 링크
    ● 수정한 이벤트 데이터
```
---

# Account 도메인 추가

OAuth2로 인증하려면 일단 Account부터
- id
- email
- password
- roles

AccountRoles
- ADMIN, USER

JPA 맵핑
- @Table

JPA enumeration collection mapping
```
@ElementCollection(fetch = FetchType.EAGER)
@Enumerated(EnumType.STRING) private
Set<AccountRole> roles;
```

Event에 onwer 추가
```
@ManyToOne
Account manager;
```
---

# 스프링 시큐리티 적용

스프링 시큐리티
- 웹 시큐리티 (Filter 기반 시큐리티)
- 메서드 시큐리티
- 이 둘 다 Security Interceptor를 사용합니다
    - 리소스에 접근을 허용할 것이냐 말것이냐를 결정하는 로직
    
의존성 추가
```
implementation 'org.springframework.security.oauth.boot:spring-security-oauth2-autoconfigure:2.1.3.RELEASE'
```
- 테스트 다 깨짐 (401 Unauthorized)

UserDetailesService 구현
- 예외 테스트하기
    - @Test(expected)
    - @Rule ExpectedException
    - try-catch
- assertThat    
---

# 스프링 시큐리티 기본 설정

시큐리티 필터를 적용하지 않음
- /docs/index.html

로그인 없이 가능
- GET /api/events
- GET /api/events/{id}

로그인 해야 접근 가능
- POST /api/events
- PUT /api/events
- 나머지 전부

스프링 시큐리티 Oauth 2.0
- AuthorizationServer: OAuth2 토큰 발행(/oauth/token) 및 토큰 인증(/oauth/authorize)
    - Oder 0 (리소스 서버 보다 우선 순위가 높다.)
- ResourceServer: 리소스 요청 인증 처리 (OAuth 2 토큰 검사)
    - Oder 3 (이 값은 현재 고칠 수 없음)
    
스프링 시큐리티 설정
- @EnableWebSecurity
- @EnableGlobalMethodSecurity
- extends WebSecurityConfigurerAdapter
- PasswordEncoder: PasswordEncoderFactories.createDelegatingPassworkEncoder()
- TokenStore: InMemoryTokenStore
- AuthenticationManagerBean
- configure(AuthenticationManagerBuidler auth)
    - userDetailsService
    - passwordEncoder

- configure(HttpSecurity http)
    - /docs/**: permitAll
- configure(WebSecurty web)
    - ignore ■ /docs/**
    
- /favicon.ico

- PathRequest.toStaticResources() 사용하기

---

# 스프링 시큐리티 OAuth2 설정 : 인증 서버 설정

---

# 스프링 시큐리티 OAuth2 설정 : 리소스 서버 설정