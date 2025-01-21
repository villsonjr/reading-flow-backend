package com.ulk.readingflow.domain.constants;

import com.ulk.readingflow.api.exceptions.InstantiationNotAllowedException;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;

public final class SystemConstants {

    @Autowired
    private MessageUtils messagesUtils;

    public static final String ALLOW_PATHS = "*";
    public static final long EXPIRATION_TIME = 3600;

    public static final String GOOGLE_CREDENTIALS_FILEPATH = "/credentials/drive-creden.json";

    public static final String APPLICATION_JSON = "application/json";
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer";

    public static final String SECURITY_SCHEME = "bearerAuth";



    public static final Integer TOKEN_EXPIRATION_TIME_DAYS = 1;
    public static final Integer TOKEN_INDEX = 7;

    public static final String[] WHITE_LIST_URL = {
            // PUBLIC
            "/v1/public/**",

            // AUTHENTICATION END POINTS
            "/v1/auth/sign-up",
            "/v1/auth/sign-in",
            "/v1/auth/sign-out",

            // DOCUMENTATIONS
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",

            // SECURITY AND CONFIGURATIONS
            "/configuration/ui",
            "/configuration/security",

            // WEB JARS
            "/webjars/**"
    };

    private SystemConstants() {
        throw new InstantiationNotAllowedException(this.messagesUtils.getMessage(SystemMessages.INSTANTIATION_NOT_ALLOWED_MESSAGE));
    }
}
