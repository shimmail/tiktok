package org.example.tiktok.pojo.dto;

import lombok.Data;

@Data
public class VideoSearch {
    private String keywords;
    private String username;
    private Integer fromDate;
    private Integer toDate;

    public VideoSearch(String keywords, String username, Integer fromDate, Integer toDate) {
        this.keywords = keywords;
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
