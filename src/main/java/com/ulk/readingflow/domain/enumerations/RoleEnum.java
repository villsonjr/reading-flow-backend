package com.ulk.readingflow.domain.enumerations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ulk.readingflow.api.exceptions.UnexpectedValueException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RoleEnum {

    USER("USER"),
    MODERATOR("MODERATOR"),
    ADMINISTRATOR("ADMINISTRATOR");

    private String description;

    @JsonCreator
    public static RoleEnum fromString(String s) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.description.equalsIgnoreCase(s)) {
                return role;
            }
        }
        throw new UnexpectedValueException(SystemMessages.UNEXPECTED_VALUE.toString());
    }
}
