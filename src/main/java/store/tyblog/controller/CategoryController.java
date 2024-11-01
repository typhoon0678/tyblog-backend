package store.tyblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.tyblog.dto.category.CategoryRequestDto;
import store.tyblog.dto.category.CategoryResponseDto;
import store.tyblog.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "로그인한 회원의 카테고리 목록", description = "글 작성 시, 로그인한 회원의 카테고리 목록 불러오기")
    public ResponseEntity<List<CategoryResponseDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @Operation(summary = "특정 회원의 카테고리 목록", description = "특정 유저의 블로그 접속 시, 블로그의 카테고리 목록 불러오기")
    @GetMapping("/{username}")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByUsername(@PathVariable String username) {
        return ResponseEntity.ok(categoryService.getCategoriesByUsername(username));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.create(categoryRequestDto);
        return ResponseEntity.ok().build();
    }
}
