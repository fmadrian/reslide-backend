package com.mygroup.backendReslide.security;


import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // The client sends the token to the server as part of the request headers.
        // Intercept the request and fetch the token from the request headers.
        // String jwt = getJwtFromRequest(request);

        // Validate the token with the public key of the keystore.
        // jwtProvider.validateToken(jwt); // See above.

        // If we have a valid token, we have to load the user
        // from the database and set the user details in the
        // Spring security context.

        String jwt = getJwtFromRequest(request);
        // If we get a token from the request and the token is valid
        if(StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)){
            // We get the username from the token
            String username = jwtProvider.getUsernameFromJwt(jwt);
            // We load the user from the database and sets its details into an UserDetails object
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // We create the UsernamePasswordAuthenticationToken
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // We build details using a WebAuthenticationDetailsSource
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // We are storing the username+passwordAuthenticationToken inside the Security Context.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // We have to call a filter chain
        // Spring finds the user details inside the security context
        // and fulfill our request. If not it throws an Exception.
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request){
        // HTTP REQUEST as input
        // Get the auth header from the request
        String bearerToken = request.getHeader("Authorization");
        // If the authorization header is not empty and starts with 'Bearer '
        // Extract the token (delete the bearer part) and return it.
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }return bearerToken;
    }
}
