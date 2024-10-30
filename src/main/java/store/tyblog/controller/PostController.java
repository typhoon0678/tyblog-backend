package store.tyblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.tyblog.common.util.S3Util;
import store.tyblog.dto.post.ImageDto;
import store.tyblog.dto.post.ImageUrlResponseDto;
import store.tyblog.dto.post.PostRequestDto;
import store.tyblog.dto.post.PostResponseDto;
import store.tyblog.service.PostService;

import java.io.IOException;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final S3Util s3Util;

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

    @PostMapping(value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이미지 업로드 API", description = "이미지 파일을 전달하면, 업로드 후 url을 전달")
    public ResponseEntity<ImageUrlResponseDto> image(ImageDto imageDto) throws IOException {
        String url = s3Util.uploadS3(imageDto.getImage());
        return ResponseEntity.ok().body(new ImageUrlResponseDto(url));
    }
}
