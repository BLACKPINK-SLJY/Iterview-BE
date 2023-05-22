package server.api.iterview.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.question.Tag;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {
    private String tag;

    public static TagDto of(Tag tag){
        return TagDto.builder()
                .tag(tag.getName())
                .build();
    }
}
