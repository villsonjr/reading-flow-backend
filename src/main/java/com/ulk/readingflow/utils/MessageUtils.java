package com.ulk.readingflow.utils;

import com.ulk.readingflow.domain.enumerations.SystemMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtils {

    private final MessageSource messageSource;

    @Autowired
    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(SystemMessages message, Locale locale) {
        return this.messageSource.getMessage(message.getKeyMessage(), null, locale);
    }

    public String getMessage(SystemMessages message) {
        return this.messageSource.getMessage(message.getKeyMessage(), null, LocaleContextHolder.getLocale());
    }

    public String getMessage(SystemMessages message, Locale locale, String... params) {
        return this.messageSource.getMessage(message.getKeyMessage(), params, locale);
    }

    public String getMessage(SystemMessages message, String... params) {
        return this.getMessage(message, LocaleContextHolder.getLocale(), params);
    }
}
