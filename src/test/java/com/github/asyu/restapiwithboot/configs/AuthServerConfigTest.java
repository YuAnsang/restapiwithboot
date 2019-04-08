package com.github.asyu.restapiwithboot.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.asyu.restapiwithboot.accounts.AccountService;
import com.github.asyu.restapiwithboot.common.AppProperties;
import com.github.asyu.restapiwithboot.common.BaseControllerTest;
import com.github.asyu.restapiwithboot.common.TestDescription;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;
    
    @Autowired
    AppProperties appProperties;
    
    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        // When && Then
        this.mockMvc.perform(post("/oauth/token")
                             .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                             .param("username", appProperties.getUserUsername())
                             .param("password", appProperties.getUserPassword())
                             .param("grant_type", "password")
                             )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}
