package server.api.iterview.service.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.bookmark.Bookmark;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Category;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;
import server.api.iterview.dto.question.QuestionDto;
import server.api.iterview.repository.BookmarkRepository;
import server.api.iterview.repository.QuestionRepository;
import server.api.iterview.repository.TagRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.question.QuestionResponseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TagRepository tagRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public QuestionDto insertTerm(String content, String categoryString, String keywords, String tags, Integer level) {

        Object[] tagStringList = Arrays.stream(tags.split(",")).map(String::trim).toArray();
        List<Tag> tagEntityList = new ArrayList<>();
        for(Object tagElem : tagStringList){
            tagEntityList.add(this.getTag((String)tagElem));
        }

        Category category;
        try {
            category = Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        Question question = Question.builder()
                .content(content)
                .category(category)
                .keywords(keywords)
                .level(level)
                .tags(tagEntityList)
                .build();

        questionRepository.save(question);

        return QuestionDto.of(question);
    }

    @Transactional
    public Tag getTag(String tagName){
        Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                .orElse(null);

        if(tag == null){
            tag = Tag.builder().name(tagName).build();
            tagRepository.save(tag);
        }

        return tag;
    }

    @Transactional
    public void deleteQuestion(Long id) {
        try {
            questionRepository.deleteById(id);
        }catch (EmptyResultDataAccessException e){
            throw new BizException(QuestionResponseType.NOT_EXIST);
        }
    }

    @Transactional
    public List<QuestionDto> getAllQuestion(Member member) {
        List<Question> questions = questionRepository.findAll();

        return getQuestionDtosFromQuestions(questions, member);
    }

    @Transactional
    public List<QuestionDto> getQuestionsByCategory(String categoryString, Member member) {
        Category category;
        try{
            category = Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        List<Question> questions = questionRepository.findByCategory(category);

        if(questions.isEmpty()){
            return new ArrayList<>();
        }

        return getQuestionDtosFromQuestions(questions, member);
    }

    @Transactional
    public List<QuestionDto> getQuestionDtosFromQuestions(List<Question> questions, Member member){
        List<QuestionDto> questionDtos = new ArrayList<>();

        for(Question question: questions){
            QuestionDto questionDto = QuestionDto.of(question);
            Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, question)
                    .orElse(new Bookmark());

            questionDto.setBookmarked(bookmark.getStatus());
            questionDtos.add(questionDto);
        }

        return questionDtos;
    }

    @Transactional
    public List<QuestionDto> getSearchResults(String word, Member member) {

        List<Question> searchResults = questionRepository.getSearchResults(word);

        List<QuestionDto> questionDtos = this.getQuestionDtosFromQuestions(searchResults, member);

        return questionDtos;
    }
}
