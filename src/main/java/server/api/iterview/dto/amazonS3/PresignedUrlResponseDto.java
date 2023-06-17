package server.api.iterview.dto.amazonS3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponseDto {
    private String presignedUrl;
}
