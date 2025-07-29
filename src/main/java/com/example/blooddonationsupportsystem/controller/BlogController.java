package com.example.blooddonationsupportsystem.controller;
import com.example.blooddonationsupportsystem.dtos.request.blog.CreateBlogRequest;
import com.example.blooddonationsupportsystem.dtos.request.blog.UpdateBlogRequest;
import com.example.blooddonationsupportsystem.models.User;
import com.example.blooddonationsupportsystem.repositories.UserRepository;
import com.example.blooddonationsupportsystem.service.blog.IBlogService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final IBlogService blogService;
    private final UserRepository userRepository;


    @PreAuthorize("true")
    @PostMapping
    public ResponseEntity<?> createBlog(
        @RequestBody CreateBlogRequest request,
        Principal principal
    ) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return blogService.createBlog(request, user.getId());
    }

    @PreAuthorize("true")
    @GetMapping
    public ResponseEntity<?> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return blogService.getAllBlogs(page, size);
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

@GetMapping("/my-blogs")
@PreAuthorize("true")
public ResponseEntity<?> getMyBlogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Principal principal
) {
    String email = principal.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return blogService.getMyBlogs(user.getId(), page, size);
}

}
