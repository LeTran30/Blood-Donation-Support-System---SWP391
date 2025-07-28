package com.example.blooddonationsupportsystem.service.blog;

import com.example.blooddonationsupportsystem.dtos.request.blog.CreateBlogRequest;
import com.example.blooddonationsupportsystem.dtos.request.blog.UpdateBlogRequest;
import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
import com.example.blooddonationsupportsystem.models.Blog;
import com.example.blooddonationsupportsystem.repositories.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService implements IBlogService {
    private final BlogRepository blogRepository;
    @Override
    public ResponseEntity<?> createBlog(CreateBlogRequest request) {
        Blog blog = Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();
        blogRepository.save(blog);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.CREATED)
                        .message("Blog created successfully")
                        .data(blog)
                        .build()
        );
    }
    @Override
    public ResponseEntity<?> getAllBlogs() {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Successfully retrieved all blogs")
                        .data(blogRepository.findAll())
                        .build()
        );
    }
    @Override
    public ResponseEntity<?> getBlogById(Integer id) {
        return blogRepository.findById(id)
                .map(blog -> ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Successfully retrieved blog")
                                .data(blog)
                                .build()
                ))
                .orElse(ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Blog not found")
                                .build()
                ));
    }
    @Override
    public ResponseEntity<?> updateBlog(Integer id, UpdateBlogRequest request) {
        return blogRepository.findById(id)
                .map(blog -> {
                    blog.setTitle(request.getTitle());
                    blog.setContent(request.getContent());
                    blogRepository.save(blog);
                    return ResponseEntity.ok(
                            ResponseObject.builder()
                                    .status(HttpStatus.OK)
                                    .message("Blog updated successfully")
                                    .data(blog)
                                    .build()
                    );
                })
                .orElse(ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Blog not found")
                                .build()
                ));
    }
    @Override
    public ResponseEntity<?> deleteBlog(Integer id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return ResponseEntity.ok(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Blog deleted")
                            .build());
        } else {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND)
                            .message("Blog not found")
                            .build()
            );  
        }

    }
}
