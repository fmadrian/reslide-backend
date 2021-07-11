package com.mygroup.backendReslide.security;

import com.mygroup.backendReslide.exceptions.JwtSecurityException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;

@Service
public class JwtProvider {
    private KeyStore keyStore; // Key store.
    @Value("${jwt.expiration.time}") // Inject the value set in (application.properties)
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init(){
        try{
            // We are providing a keystore instance of type jks.
            keyStore = KeyStore.getInstance("JKS");
            // We are getting the input from the keystore file.
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            // We provide the file with the password to load it.
            keyStore.load(resourceAsStream, "secret".toCharArray());
        }catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new JwtSecurityException("Exception occurred while loading keystore", e);
        }
    }

    // Generates the JWT token from 0 when a new user logs in.
    public String generateToken(Authentication authentication){
        // Cast the authentication object to org.springframework.security.core.userdetails.User
        User principal = (User)authentication.getPrincipal();
        // Build and return the signed JWT token in the form of a string .
        return Jwts.builder()
                .setSubject(principal.getUsername()) // Uses the username as a subject.
                .setIssuedAt(Date.from(Instant.now())) // When the token was issued
                .signWith(getPrivateKey()) // This token was signed with...
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis))) // Expiration time set in the token.
                .compact();
        // In this example, we will asymmetric encryption
        // This means that we'll use a keystore to sign the JWT
        // Specifically the private key of keystore to sign the JWT.
    }

    // Used to generate a new token for a user, when the previous token has expired
    // We have to use username as a parameter, we can't get from the SECURITY CONTEXT HOLDER.
    // Because If the previous token has expired, there won't be any user information inside the SECURITY CONTEXT HOLDER
    public String generateTokenWithUsername(String username) {
        return Jwts.builder()
                .setSubject(username) // Security Context Holder doesn't have any user information.
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey(){
        try {
            // To read the private key we need the alias and the password of the keystore.
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new JwtSecurityException("Exception occurred while retrieving public key from keystore", e);
        }
    }

    // The token is created using asymmetric encryption
    // By signing them with the PRIVATE key from the keystore
    // We take token and validate it using the PUBLIC key
    public boolean validateToken(String jwt) {
        // parseClaimsJws verifies the token with the returned public key.
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey(){
        // Returns the public key from the keystore.
        try {
            // 1. We need to get the certificate with the alias of the keystore
            // 2. When we have the certificate, we get the public key.
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new JwtSecurityException("Exception occurred while " +
                    "retrieving public key from keystore", e);
        }
    }

    public String getUsernameFromJwt(String token) {
        // Decodes the token and gets the subject that includes the username
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }

}
