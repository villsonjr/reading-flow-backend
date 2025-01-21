package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.ulk.readingflow.domain.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {

    private String name;

    public static CategoryDTO fromEntity(Category category) {
        return CategoryDTO.builder()
                .name(category.getName())
                .build();
    }



}
