package store.tyblog.dto.category;

import lombok.Data;
import store.tyblog.entity.Category;

@Data
public class CategoryResponseDto {

    private long id;
    private String name;

    public CategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
