package store.tyblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.tyblog.dto.post.PostRequestDto;
import store.tyblog.dto.post.PostResponseDto;
import store.tyblog.service.PostService;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostRequestDto postRequestDto) {
        postService.create(postRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}")
    public ResponseEntity<Page<PostResponseDto>> getPosts(
            @PathVariable String username,
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt", "id").descending());
        Page<PostResponseDto> postPage = postService.getPosts(username, pageable);
        return ResponseEntity.ok().body(postPage);
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<PostResponseDto>> getRecentPosts(
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt", "id").descending());
        Page<PostResponseDto> postPage = postService.getRecentPosts(pageable);
        return ResponseEntity.ok().body(postPage);
    }
}
