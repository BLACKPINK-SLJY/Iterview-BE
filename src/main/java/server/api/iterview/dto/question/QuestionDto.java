package server.api.iterview.dto.question;

import lombok.*;
import server.api.iterview.domain.answer.AnsweredStatus;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String content;
    private String category;
    private List<String> keywords;
    private List<String> tags;
    private Integer level;
    private BookmarkStatus bookmarked;
    private Long entireBookmarkedCount;
    private AnsweredStatus answered;

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
                .questionId(question.getId())
                .content(question.getContent())
                .category(question.getCategory().name())
                .keywords(keywordStrings)
                .tags(tagStrings)
                .level(question.getLevel())
                .bookmarked(BookmarkStatus.N)
                .answered(AnsweredStatus.N)
                .build();
    }
}
