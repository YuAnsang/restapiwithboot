package com.github.asyu.restapiwithboot.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import com.github.asyu.restapiwithboot.accounts.Account;
import com.github.asyu.restapiwithboot.accounts.AccountRepository;
import com.github.asyu.restapiwithboot.accounts.AccountRole;
import com.github.asyu.restapiwithboot.accounts.AccountService;
import com.github.asyu.restapiwithboot.common.AppProperties;
import com.github.asyu.restapiwithboot.common.BaseControllerTest;
import com.github.asyu.restapiwithboot.common.TestDescription;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;
    
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    AppProperties appProperties;
    
    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }
    
    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(Matchers.is(EventStatus.DRAFT.name())))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query"),
                                linkWithRel("update-event").description("link to update")
                                //linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields( 
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query"),
                                fieldWithPath("_links.update-event.href").description("link to update")
                                //fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;   
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_BadRequest() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();
        
        mockMvc.perform(post("/api/events/")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                        .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();
        
        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
                ;
    }
    
    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void getEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        
        // When & Then
        this.mockMvc.perform(get("/api/events")
                            .param("page", "1")
                            .param("size", "10")
                            .param("sort", "name,DESC"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("page").exists())
                    .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    //.andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("query-events"))
                    ;
    }
    
    @Test
    @TestDescription("기존의 이벤트를 하나 조회 하기")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);
        
        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").exists())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    //.andExpect(jsonPath("_links.profile").exists())
                    ;
    }
    
    @Test
    @TestDescription("없는 이벤트 조회했을 때 404 응답받기")
    public void getNotExistEvent() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                    .andExpect(status().isNotFound())
                    ;
    }
    
    @Test
    @TestDescription("정상적으로 이벤트 업데이트 하기")
    public void updateEvent() throws Exception {
        // Given
        Event event = generateEvent(200);
        
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "updated event";
        eventDto.setName(eventName);
        
        // When && Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(status().isOk())
                ;
    }
    
    @Test
    @TestDescription("입력 값이 비어있는 경우 이벤트 수정 실패")
    public void updateEvent_badRequest() throws Exception {
        Event event = generateEvent(200);
        
        EventDto eventDto = new EventDto();
        
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                        .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("입력 값이 잘못되어 있는 경우 이벤트 수정 실패")
    public void updateEvent_badRequest_Wrong() throws Exception {
        // Given
        Event event = generateEvent(200);
        
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);
        
        // When && Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                        .andExpect(status().isBadRequest());
    }
    
    @Test
    @TestDescription("존재하지 않은 이벤트 수정 실패")
    public void updateEvent_notFound() throws Exception {
        // Given
        Event event = generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        
        // When & Then
        mockMvc.perform(put("/api/events/123123")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                        .andExpect(status().isNotFound());
    }
    
    public Event generateEvent(int i) {
        Event event = Event.builder()
                            .name("Spring")
                            .description("REST API Development with Spring")
                            .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                            .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                            .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                            .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                            .basePrice(100)
                            .maxPrice(200)
                            .limitOfEnrollment(100)
                            .location("강남역 D2 스타텁 팩토리")
                            .free(false)
                            .offline(true)
                            .build();
        
        eventRepository.save(event);
        return event;
    }
    
    public String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }
    
    public String getAccessToken() throws Exception {
        Account account = Account.builder()
                                .email(appProperties.getUserUsername())
                                .password(appProperties.getUserPassword())
                                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
        this.accountService.saveAccount(account);
        
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                             .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                             .param("username", appProperties.getUserUsername())
                             .param("password", appProperties.getUserPassword())
                             .param("grant_type", "password")
                             );
        
        MockHttpServletResponse response = perform.andReturn().getResponse();
        String resultString = response.getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(resultString).get("access_token").toString();
    }

}