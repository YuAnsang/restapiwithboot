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
import com.github.asyu.restapiwithboot.common.AppProperties;

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
    
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;
            
            @Autowired
            AppProperties appProperties;
            
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account admin = Account.builder()
                                .email(appProperties.getAdminUsername())
                                .password(appProperties.getAdminPassword())
                                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
                accountService.saveAccount(admin);
                
                Account user = Account.builder()
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .roles(Set.of(AccountRole.USER))
                        .build();
                accountService.saveAccount(user);
            }
        };
    }
}
