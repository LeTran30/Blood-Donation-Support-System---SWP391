package com.example.blooddonationsupportsystem.service.blog;

import org.springframework.http.ResponseEntity;

import com.example.blooddonationsupportsystem.dtos.request.blog.CreateBlogRequest;
import com.example.blooddonationsupportsystem.dtos.request.blog.UpdateBlogRequest;

public interface IBlogService {

    ResponseEntity<?> createBlog(CreateBlogRequest request, Integer authorId);

    ResponseEntity<?> getAllBlogs(int page, int size);

    ResponseEntity<?> getBlogById(Integer id);

    ResponseEntity<?> updateBlog(Integer id, UpdateBlogRequest request);

    ResponseEntity<?> deleteBlog(Integer id);

    ResponseEntity<?> getMyBlogs(Integer userId, int page, int size);
}
