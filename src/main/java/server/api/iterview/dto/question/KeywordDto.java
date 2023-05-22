package server.api.iterview.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {
    private String keyword;

    public static KeywordDto of(String keyword){
        return KeywordDto.builder()
                .keyword(keyword)
                .build();
    }
}
