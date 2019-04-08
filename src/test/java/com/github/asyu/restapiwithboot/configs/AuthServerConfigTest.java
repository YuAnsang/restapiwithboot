package com.github.asyu.restapiwithboot.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.asyu.restapiwithboot.accounts.Account;
import com.github.asyu.restapiwithboot.accounts.AccountRole;
import com.github.asyu.restapiwithboot.accounts.AccountService;
import com.github.asyu.restapiwithboot.common.BaseControllerTest;
import com.github.asyu.restapiwithboot.common.TestDescription;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;
    
    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        // Given
        String username = "asyu@gmail.com";
        String password = "ml4151";
        Account account = Account.builder().email(username)
                                .password(password)
                                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
        this.accountService.saveAccount(account);
        
        String clientId = "myApp";
        String clientSecret = "pass";
        
        // When && Then
        this.mockMvc.perform(post("/oauth/token")
                             .with(httpBasic(clientId, clientSecret))
                             .param("username", username)
                             .param("password", password)
                             .param("grant_type", "password")
                             )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}
