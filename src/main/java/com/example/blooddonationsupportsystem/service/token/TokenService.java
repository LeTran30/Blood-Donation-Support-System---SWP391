package com.example.blooddonationsupportsystem.service.token;

import com.example.blooddonationsupportsystem.models.Token;
import com.example.blooddonationsupportsystem.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private final TokenRepository tokenRepository;


    @Override
    @Scheduled(fixedRate = 5000)
    public void deleteExpiredTokens() {
        List<Token> tokenListExpried = tokenRepository.findExpiredAndRevokedTokens();
        if(tokenListExpried.isEmpty()){
            tokenRepository.deleteAll(tokenListExpried);
        }
    }
}
