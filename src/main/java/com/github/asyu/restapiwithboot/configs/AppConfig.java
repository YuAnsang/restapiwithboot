package com.github.asyu.restapiwithboot.configs;

import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.asyu.restapiwithboot.accounts.Account;
import com.github.asyu.restapiwithboot.accounts.AccountRole;
import com.github.asyu.restapiwithboot.accounts.AccountService;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    /*
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            
            @Autowired
            AccountService accountService;
            
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder().email("asyu@gmail.com").password("ml4151")
                                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
                accountService.saveAccount(account);
            }
        };
    }
    */
}
