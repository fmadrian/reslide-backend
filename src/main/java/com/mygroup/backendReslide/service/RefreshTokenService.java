package com.mygroup.backendReslide.service;

import com.mygroup.backendReslide.exceptions.InvalidRefreshTokenException;
import com.mygroup.backendReslide.model.RefreshToken;
import com.mygroup.backendReslide.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void validateRefreshToken(String refreshToken){
        // Searches the refresh token. If it doesn't exists, it isn't valid.
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->{throw new InvalidRefreshTokenException();});
    }

    public RefreshToken generateRefreshToken(){
        // Creates a new refresh token and stores it into the database.
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString()); // 128 bit random UUID as a refresh token
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken){
        refreshTokenRepository.deleteByToken(refreshToken);
    }

}
