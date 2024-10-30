package store.tyblog.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostRequestDto {

    private String title;
    private String content;
    private List<Long> categoryIds;
}
