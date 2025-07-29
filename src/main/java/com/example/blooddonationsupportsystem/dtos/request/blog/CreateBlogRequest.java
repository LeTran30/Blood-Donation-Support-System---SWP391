package com.example.blooddonationsupportsystem.dtos.request.blog;

import lombok.Data;

@Data
public class CreateBlogRequest {
    private String title;
    private String content;
}