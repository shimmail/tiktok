package org.example.tiktok.pojo.dto;

import lombok.Data;

@Data
public class VideoSearch {
    private String keywords;
    private String username;
    private Integer fromDate;
    private Integer toDate;
    private Integer sortBy;

    public VideoSearch(String keywords, String username, Integer fromDate, Integer toDate, Integer sortBy) {
        this.keywords = keywords;
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.sortBy = sortBy;
    }
}
