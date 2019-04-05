package com.github.asyu.restapiwithboot.accounts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Autowired
    AccountService accountService;
    
    @Autowired
    AccountRepository accountRepository;
    
    @Test
    public void findByUsername() {
        // Given
        String password = "m4151";
        String username = "kahlman@naver.com";
        Account account = Account.builder().email(username)
                                    .password(password)
                                    .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                    .build();
        
        accountRepository.save(account);
        
        // When
        UserDetailsService userDetailsServce = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsServce.loadUserByUsername(username);
        
        // Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }
    
    @Test
    public void findByUsernameFail() {
        // Given
        String username = "random@gmail.com";
        
        // When && Then
        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch(UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username);
        }
    }
    
    @Test
    public void findByUsernameFail_useExpectedException() {
        // Expected
        String username = "random@gmail.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));
        
        // When
        accountService.loadUserByUsername(username);
    }
}
