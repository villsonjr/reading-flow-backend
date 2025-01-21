package com.ulk.readingflow.domain.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StatusEnum {

    CREATED("CREATED"),
    IN_PROGRESS("IN_PROGRESS"),
    CANCELED("CANCELED"),
    COMPLETE("COMPLETE"),

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    EXPIRED("EXPIRED"),

    REVOKED("REVOKED"),
    CREDENTIALS_EXPIRED("CREDENTIALS_EXPIRED"),
    BLOCKED("BLOCKED");

    private String description;

}
