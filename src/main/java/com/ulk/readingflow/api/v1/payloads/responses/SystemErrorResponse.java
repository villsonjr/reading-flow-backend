package com.ulk.readingflow.api.v1.payloads.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemErrorResponse {

    private HttpStatus httpStatus;
    private String errorKey;
    private List<String> details;

}
