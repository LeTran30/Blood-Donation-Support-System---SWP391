        package com.example.blooddonationsupportsystem.service.blog;

        import com.example.blooddonationsupportsystem.dtos.request.blog.CreateBlogRequest;
        import com.example.blooddonationsupportsystem.dtos.request.blog.UpdateBlogRequest;
        import com.example.blooddonationsupportsystem.dtos.responses.ResponseObject;
        import com.example.blooddonationsupportsystem.dtos.responses.blog.BlogResponse;
        import com.example.blooddonationsupportsystem.models.Blog;
        import com.example.blooddonationsupportsystem.models.User;
        import com.example.blooddonationsupportsystem.repositories.BlogRepository;
        import com.example.blooddonationsupportsystem.repositories.UserRepository;

        import lombok.RequiredArgsConstructor;

        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.domain.Sort;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UsernameNotFoundException;
        import org.springframework.stereotype.Service;

        @Service
        @RequiredArgsConstructor
        public class BlogService implements IBlogService {
        private final BlogRepository blogRepository;
        private final UserRepository userRepository;
        @Override
        public ResponseEntity<?> createBlog(CreateBlogRequest request, Integer authorId) {
        User author = userRepository.findById(authorId)
                .orElse(null);

        if (author == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ResponseObject.builder()
                                .status(HttpStatus.BAD_REQUEST)
                                .message("Không tìm thấy tác giả có ID: " + authorId)
                                .build()
                );
        }

        Blog blog = Blog.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author) 
                .build();

                Blog savedBlog = blogRepository.save(blog);
                BlogResponse response = toBlogResponse(savedBlog);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        ResponseObject.builder()
                                .status(HttpStatus.CREATED)
                                .message("Tạo blog thành công")
                                .data(response)
                                .build()
                );

        }

        @Override
        public ResponseEntity<?> getAllBlogs(int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
                Page<Blog> blogs = blogRepository.findAll(pageable);
                Page<BlogResponse> blogResponses = blogs.map(this::toBlogResponse);

                return ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Truy xuất tất cả blog thành công")
                                .data(blogResponses)
                                .build()
                );
        }
        
            @Override
        public ResponseEntity<?> getBlogById(Integer id) {
                return blogRepository.findById(id)
                        .map(blog -> ResponseEntity.ok(
                                ResponseObject.builder()
                                        .status(HttpStatus.OK)
                                        .message("Truy xuất blog thành công")
                                        .data(toBlogResponse(blog))
                                        .build()
                        ))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ResponseObject.builder()
                                        .status(HttpStatus.NOT_FOUND)
                                        .message("Không tìm thấy blog")
                                        .build()
                        ));
        }

        @Override
        public ResponseEntity<?> updateBlog(Integer id, UpdateBlogRequest request) {
                return blogRepository.findById(id)
                        .map(blog -> {
                        blog.setTitle(request.getTitle());
                        blog.setContent(request.getContent());
                        Blog updated = blogRepository.save(blog);
                        return ResponseEntity.ok(
                                ResponseObject.builder()
                                        .status(HttpStatus.OK)
                                        .message("Cập nhật blog thành công")
                                        .data(toBlogResponse(updated))
                                        .build()
                        );
                        })
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                ResponseObject.builder()
                                        .status(HttpStatus.NOT_FOUND)
                                        .message("Không tìm thấy blog")
                                        .build()
                        ));
        }

        @Override
        public ResponseEntity<?> deleteBlog(Integer id) {
                if (blogRepository.existsById(id)) {
                blogRepository.deleteById(id);
                return ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.OK)
                                .message("Xóa blog thành công")
                                .build()
                );
                } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy blog")
                                .build()
                );
                }
        }

        @Override
        public ResponseEntity<?> getMyBlogs(Integer userId, int page, int size) {
        // Lấy username hiện tại từ context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));


        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        Page<Blog> blogPage = blogRepository.findByAuthorId(currentUser.getId(), pageable);

        if (blogPage.isEmpty()) {
                return ResponseEntity.ok(
                        ResponseObject.builder()
                                .status(HttpStatus.NOT_FOUND)
                                .message("Không tìm thấy blog nào của người dùng hiện tại")
                                .build()
                );
        }
        Page<BlogResponse> blogResponses = blogPage.map(this::toBlogResponse);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Đã tìm thấy các blog của người dùng hiện tại")
                        .data(blogResponses)
                        .build()
        );
        }
        public BlogResponse toBlogResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .authorName(blog.getAuthor().getFullName()) // hoặc getEmail()
                .build();
        }

}
