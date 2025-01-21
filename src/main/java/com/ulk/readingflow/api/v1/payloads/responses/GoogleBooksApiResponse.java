package com.ulk.readingflow.api.v1.payloads.responses;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoogleBooksApiResponse implements Serializable {

    private int totalItems;
    private List<Item> items;

    @Data
    public static class Item {
        private VolumeInfo volumeInfo;
    }

    @Data
    public static class VolumeInfo {
        private String title;
        private String description;
        private Integer pageCount;
        private List<String> authors;
    }

}
