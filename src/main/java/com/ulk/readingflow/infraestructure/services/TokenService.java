package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.domain.entities.Token;
import com.ulk.readingflow.infraestructure.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public List<Token> getValidTokens(UUID userID) {
        if (userID == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return this.tokenRepository.findAllValidTokensByUserId(userID);
    }

    public Token save(Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        return this.tokenRepository.save(token);
    }

    public void saveTokens(List<Token> tokenList) {
        if (tokenList == null) {
            throw new IllegalArgumentException("Token list must not be null");
        }
        if (tokenList.contains(null)) {
            throw new IllegalArgumentException("Token list must not contain null values");
        }
        this.tokenRepository.saveAll(tokenList);
    }
}
