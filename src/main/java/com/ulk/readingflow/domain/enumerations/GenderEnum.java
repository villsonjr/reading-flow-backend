package com.ulk.readingflow.domain.enumerations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ulk.readingflow.api.exceptions.UnexpectedValueException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum GenderEnum {

    MALE("MALE"),
    FEMALE("FEMALE"),
    NOT_INFORMED("NOT INFORMED");

    private String description;

    @JsonCreator
    public static GenderEnum fromString(String s) {
        if (null == s) {
            return NOT_INFORMED;
        } else {
            if (s.equalsIgnoreCase("MASCULINO") ||
                    s.equalsIgnoreCase("M") ||
                    s.equalsIgnoreCase(MALE.getDescription())
            ) {
                return MALE;
            } else if (s.equalsIgnoreCase("FEMININO") ||
                    s.equalsIgnoreCase("S") ||
                    s.equalsIgnoreCase(FEMALE.getDescription())
            ) {
                return FEMALE;
            } else if (s.equalsIgnoreCase("NÃO INFORMADO") ||
                    s.equalsIgnoreCase("NI") ||
                    s.equalsIgnoreCase(NOT_INFORMED.getDescription())
            ) {
                return NOT_INFORMED;
            } else {
                throw new UnexpectedValueException(SystemMessages.UNEXPECTED_VALUE.toString());
            }
        }
    }
}
