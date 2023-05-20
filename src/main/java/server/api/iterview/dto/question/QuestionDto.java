package server.api.iterview.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String content;
    private String category;
    private String keywords;
    private String tags;
    private Integer level;

    public static QuestionDto of(Question question){
        Optional<String> tagString =  question.getTags().stream().map(Tag::getName).reduce((x, y) -> x + ", " + y);

        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .category(question.getCategory().name())
                .keywords(question.getKeywords())
                .tags(tagString.orElse(null))
                .level(question.getLevel())
                .build();
    }
}
