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
                .antMatchers("/api/auth/**") // Allows access to GET method into the auth API without authentication token (requirement for login)
                .permitAll()
                .anyRequest()
                .authenticated();// Any request that is authenticated, is allowed.
    }
//         In summary, this allows any request (GET, POST, DELETE, ...)  TO api/auth/...
//              In this scenario any request to 'api/auth/' or 'api/auth/myexample/' is allowed for any client (with / without) authentication token
//         Allows any GET (and ONLY GET) request ONLY TO 'api/subreddit'.
//              In this case, a GET request to api/subreddit will be allowed for any client.
//              In this case, a POST request to api/subreddit won't be allowed for a client without an authentication token.
//              In this case, a GET request to api/subreddit/mysubreddit won't be allowed without an authentication token.
//         Allows any GET (and ONLY GET) request ONLY TO 'api/posts' and 'api/posts/**'.
//              In this case a GET request to: 'api/posts' or 'api/posts/32', will be allowed for any client.
//              In this case a POST request to: 'api/posts' is forbidden to clients without an authentication token.
//          Defining multiple url into one antmatchers is the equivalent to do it one by one. If we need to, we could specify the same operations for all of them.
    // Any other request to another endpoint, has to be authenticated to go through. Otherwise, it will be rejected.
//
//         When we say any request includes requests made without the Bearer authentication token.
//         If a request is allowed, but is not mapped by any controller, it will return a 404 error.
//         If a request is not allowed, it will return a 403 error.



    // Letting know Spring Security about the JWT Authentication Filter Class
    // Adding the jwtAuthenticationFilter and the UsernamePasswordAuthenticationFilter
    // Spring checks first for the access (JWT) token, then checks for the UsernamePasswordAuthentication key
    // httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    //}
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
