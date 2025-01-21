package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ulk.readingflow.domain.entities.Reading;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingDTO implements Serializable {

    private String key;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - HH:mm:ss")
    private String readingDate;
    private Integer rating;
    private BookDTO book;

    public static ReadingDTO fromEntity(Reading reading) {

        return ReadingDTO.builder()
                .key(reading.getId().toString())
                .readingDate(reading.getReadingDate().toString())
                .rating(reading.getRating())
                .book(BookDTO.fromEntity(reading.getBook()))
                .build();
    }

    @JsonIgnore
    public String getDateReport() {
        return formatDateToReport(this.readingDate);
    }

    public static String formatDateToReport(String date) {
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDateTime.parse(date, originalFormatter).format(targetFormatter);
    }
}
