package com.mygroup.backendReslide.config;

import com.mygroup.backendReslide.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // Enables the web security module
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // WebSecurityConfigurerAdapter base class that provides default security configuration, which we can  override.

    private final UserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
//         CSRF attacks. Disable it because this attacks occur when there are sessions and cookies to authenticate session information.
//         REST API are stateless (don't handle cookies/sessions for authentication)
//         JSON Web Token are used for authentication. This is why we can disable csrf safely.
        httpSecurity.cors().and() // Allows cors preflights requests to get through. Full explanation: https://www.baeldung.com/spring-security-cors-preflight
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**",
                        "/api/installation/**") // Allows access to any method into the auth and install without authentication token (requirement for login)
                .permitAll()
                .antMatchers("/v2/api-docs", // Swagger configuration
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui/",
                        "/swagger-ui/**",
                        "/webjars/**")
                .permitAll()
                .anyRequest()
                .authenticated();// Any other request HAS TO BE authenticated.
        //.permitAll();
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        // We have to create a userDetailsService implementation for the interface userDetailsService returned by the Manager
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
    // Bean for the password encoder.
    @Bean
    PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}

    // Bean for the AuthenticationManager
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
