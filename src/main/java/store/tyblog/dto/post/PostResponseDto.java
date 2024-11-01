package store.tyblog.dto.post;

import lombok.Data;
import store.tyblog.entity.Category;
import store.tyblog.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private List<String> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.author = post.getMember().getUsername();
        this.categories = post.getCategories().stream().map(Category::getName).toList();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
