package com.example.blooddonationsupportsystem.dtos.responses.blog;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogResponse {
    private Integer id;
    private String title;
    private String content;
    private String authorName;
}
