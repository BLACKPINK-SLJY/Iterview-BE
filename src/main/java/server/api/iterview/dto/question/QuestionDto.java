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
    private List<KeywordDto> keywords;
    private List<TagDto> tags;
    private Integer level;

    public static QuestionDto of(Question question){
//        Optional<String> tagString =  question.getTags().stream().map(Tag::getName).reduce((x, y) -> x + ", " + y);

        Object[] keywordStringList = Arrays.stream(question.getKeywords().split(",")).map(String::trim).toArray();
        List<KeywordDto> keywordDtos = new ArrayList<>();
        for(Object keywordString : keywordStringList){
            keywordDtos.add(KeywordDto.of((String)keywordString));
        }

        List<TagDto> tagDtos = new ArrayList<>();
        for (Tag tag : question.getTags()){
            tagDtos.add(TagDto.of(tag));
        }

        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .category(question.getCategory().name())
                .keywords(keywordDtos)
                .tags(tagDtos)
                .level(question.getLevel())
                .build();
    }
}
