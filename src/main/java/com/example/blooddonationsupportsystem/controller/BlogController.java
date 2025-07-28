package com.example.blooddonationsupportsystem.controller;
import com.example.blooddonationsupportsystem.dtos.request.blog.CreateBlogRequest;
import com.example.blooddonationsupportsystem.dtos.request.blog.UpdateBlogRequest;
import com.example.blooddonationsupportsystem.service.blog.IBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final IBlogService blogService;

    @PreAuthorize("true")
    @PostMapping
    public ResponseEntity<?> createBlog(@RequestBody CreateBlogRequest request) {
        return blogService.createBlog(request);
    }

    @PreAuthorize("true")
    @GetMapping
    public ResponseEntity<?> getAllBlogs() {
        return blogService.getAllBlogs();
    }
    @PreAuthorize("true")
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Integer id) {
        return blogService.getBlogById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("true")
    public ResponseEntity<?> updateBlog(@PathVariable Integer id, @RequestBody UpdateBlogRequest request) {
        return blogService.updateBlog(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("true")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id) {
        return blogService.deleteBlog(id);
    }
}
