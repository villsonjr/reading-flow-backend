package com.ulk.readingflow.core.security.jwt;

import com.ulk.readingflow.api.exceptions.AuthenticationFailException;
import com.ulk.readingflow.api.exceptions.InvalidHeaderException;
import com.ulk.readingflow.api.exceptions.InvalidTokenException;
import com.ulk.readingflow.domain.entities.User;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.UserRepository;
import com.ulk.readingflow.utils.MessageUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;

import static com.ulk.readingflow.domain.constants.SystemConstants.HEADER_STRING;
import static com.ulk.readingflow.domain.constants.SystemConstants.TOKEN_INDEX;
import static com.ulk.readingflow.domain.constants.SystemConstants.TOKEN_PREFIX;
import static com.ulk.readingflow.domain.constants.SystemConstants.WHITE_LIST_URL;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MessageUtils messagesUtils;
    private final RequestMatcher requestMatcher;

    @Autowired
    public JwtFilter(
            JwtService jwtService,
            UserRepository userRepository,
            MessageUtils messagesUtils
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.messagesUtils = messagesUtils;
        this.requestMatcher = new OrRequestMatcher(
                Arrays.stream(WHITE_LIST_URL)
                        .map(AntPathRequestMatcher::new)
                        .toArray(AntPathRequestMatcher[]::new)
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (!requestMatcher.matches(request)) {
                validateTokenAndAuthenticate(request);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.info("{} | {} | {}", ex.getMessage(), request.getMethod(), request.getRequestURI());
            resolver.resolveException(request, response, null, ex);
        }
    }

    private void validateTokenAndAuthenticate(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_STRING);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX) || authHeader.split(TOKEN_PREFIX)[1].trim().isEmpty()) {
            throw new InvalidHeaderException(messagesUtils.getMessage(SystemMessages.ERROR_INVALID_HEADER));
        }

        String jwtToken = authHeader.substring(TOKEN_INDEX);
        if (!jwtService.isValidToken(jwtToken)) {
            throw new InvalidTokenException(messagesUtils.getMessage(SystemMessages.ERROR_INVALID_TOKEN));
        }

        String username = jwtService.extractUsernameFromToken(jwtToken);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new AuthenticationFailException(messagesUtils.getMessage(SystemMessages.ERROR_AUTHENTICATION_FAILED)));

        log.info("User: {} | Token: {} | {} | {}", username, jwtToken, request.getMethod(), request.getRequestURI());
        authenticateUser(user, request);
    }

    private void authenticateUser(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    }
}
