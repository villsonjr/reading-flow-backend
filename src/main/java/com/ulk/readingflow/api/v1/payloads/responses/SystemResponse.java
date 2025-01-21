package com.ulk.readingflow.api.v1.payloads.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemResponse<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - hh:mm:ss")
    private LocalDateTime timeStamp;
    private String message;
    private T payload;

    public SystemResponse(String message, T payload) {
        this.timeStamp = LocalDateTime.now();
        this.message = message;
        this.payload = payload;
    }
}

