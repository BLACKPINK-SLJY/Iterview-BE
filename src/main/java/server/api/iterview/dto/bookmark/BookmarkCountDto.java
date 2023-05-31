package server.api.iterview.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkCountDto {
    private Long questionId;
    private Integer count;
}
