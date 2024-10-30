package store.tyblog.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Util {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    public String uploadS3(MultipartFile file) throws IOException {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String year = date.substring(0, 2);
        String month = date.substring(2, 4);
        String day = date.substring(4, 6);
        String folderPath = String.format("tyblog/%s/%s/%s/", year, month, day);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(folderPath + fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        if (response.sdkHttpResponse().statusCode() != 200) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.");
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + folderPath + fileName;
    }
}

