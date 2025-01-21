package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopAuthorDTO implements Serializable {

    private String name;
    private Long bookCount;

}
