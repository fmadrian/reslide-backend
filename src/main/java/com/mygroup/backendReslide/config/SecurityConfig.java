package com.mygroup.backendReslide.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity // Enables the web security module
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // WebSecurityConfigurerAdapter base class that provides default security configuration, which we can  override.
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
//         CSRF attacks. Disable it because this attacks occur when there are sessions and cookies to authenticate session information.
//         REST API are stateless (don't handle cookies/sessions for authentication)
//         JSON Web Token are used for authentication. This is why we can disable csrf safely.
        httpSecurity.cors().and() // Allows cors preflights requests to get through. Full explanation: https://www.baeldung.com/spring-security-cors-preflight
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**") // Allows access ANY method into the auth API without authentication token (requirement for login)
                .permitAll()
                .anyRequest()
                .authenticated();// Any request that is authenticated, is allowed.
    }

    // Bean for the password encoder.
    @Bean
    PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
