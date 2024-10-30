package store.tyblog.dto.post;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageDto {

    private MultipartFile image;
}
