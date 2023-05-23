package server.api.iterview.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long id;
    private String content;
    private String category;
    private List<String> keywords;
    private List<String> tags;
    private Integer level;

    public static QuestionDto of(Question question){
//        Optional<String> tagString =  question.getTags().stream().map(Tag::getName).reduce((x, y) -> x + ", " + y);

        List<String> keywordStrings = new ArrayList<>();
        for(Object keyword: Arrays.stream(question.getKeywords().split(",")).map(String::trim).toArray()){
            keywordStrings.add((String)keyword);
        }

        List<String> tagStrings = new ArrayList<>();
        for (Tag tag: question.getTags()){
            tagStrings.add(tag.getName());
        }

        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .category(question.getCategory().name())
                .keywords(keywordStrings)
                .tags(tagStrings)
                .level(question.getLevel())
                .build();
    }
}
