package server.api.iterview.service.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.answer.AnsweredStatus;
import server.api.iterview.domain.bookmark.Bookmark;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Category;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;
import server.api.iterview.dto.question.QuestionDto;
import server.api.iterview.repository.AnswerRepository;
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
    private final AnswerRepository answerRepository;

    @Transactional
    public Question findById(Long id){
        return questionRepository.findById(id)
                .orElseThrow(() -> new BizException(QuestionResponseType.NOT_EXIST));
    }

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

        return getQuestionDtosFromQuestions(questionRepository.findAll(), member);
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

        if (member != null) {
            for (Question question : questions) {
                QuestionDto questionDto = QuestionDto.of(question);
                Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, question)
                        .orElse(null);

                Answer answer = answerRepository.findByMemberAndQuestion(member, question)
                                .orElse(null);

                questionDto.setEntireBookmarkedCount(bookmarkRepository.countByQuestionAndStatus(question, BookmarkStatus.Y));
                questionDto.setBookmarked((bookmark == null) ? BookmarkStatus.N : bookmark.getStatus());
                questionDto.setAnswered((answer == null) ? AnsweredStatus.N : AnsweredStatus.Y);
                questionDtos.add(questionDto);
            }
        }else{
            for (Question question : questions) {
                QuestionDto questionDto = QuestionDto.of(question);

                questionDto.setEntireBookmarkedCount(bookmarkRepository.countByQuestionAndStatus(question, BookmarkStatus.Y));
                questionDtos.add(questionDto);
            }
        }

        return questionDtos;
    }

    @Transactional
    public List<QuestionDto> getSearchResults(String word, Member member) {

        return this.getQuestionDtosFromQuestions(questionRepository.getSearchResults(word), member);
    }

    public List<QuestionDto> getAllQuestionsOrderByLevel(Member member) {
        return this.getQuestionDtosFromQuestions(questionRepository.getAllQuestionsOrderByLevel(), member);
    }

    public List<QuestionDto> getQuestionsByCategoryOrderByLevel(String categoryString, Member member) {
        Category category;
        try{
            category = Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        List<Question> questions = questionRepository.getQuestionByCategoryOrderByLevel(category);

        if(questions.isEmpty()){
            return new ArrayList<>();
        }

        return this.getQuestionDtosFromQuestions(questions, member);
    }

    @Transactional
    public void bookmarkQuestion(Member member, Long id) {
        Question question = findById(id);
        Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, question)
                .orElse(null);

        if(bookmark == null){
            bookmarkRepository.save(Bookmark.builder()
                    .member(member)
                    .question(question)
                    .status(BookmarkStatus.Y)
                    .build());
            return;
        }

        bookmark.setStatus(BookmarkStatus.Y);
    }

    @Transactional
    public void unbookmarkQuestion(Member member, Long id){
        Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, findById(id))
                .orElse(null);

        if(bookmark == null) return;

        bookmark.setStatus(BookmarkStatus.N);
    }

    @Transactional
    public List<QuestionDto> getMyAnsweredAllQuestion(Member member) {

        return getQuestionDtosFromQuestions(questionRepository.getMyAnsweredQuestions(member), member);
    }

    @Transactional
    public List<QuestionDto> getMyAnsweredQuestionsByCategory(String categoryString, Member member) {
        Category category;
        try{
            category = Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        List<Question> questions = questionRepository.getMyAnsweredQuestionsByCategory(member, category);

        if(questions.isEmpty()){
            return new ArrayList<>();
        }

        return getQuestionDtosFromQuestions(questions, member);
    }

    @Transactional
    public List<QuestionDto> getMyBookmarkedAllQuestion(Member member) {

        return getQuestionDtosFromQuestions(questionRepository.getMyBookmarkedQuestions(member, BookmarkStatus.Y), member);
    }

    @Transactional
    public List<QuestionDto> getMyBookmarkedQuestionsByCategory(String categoryString, Member member) {
        Category category;
        try{
            category = Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        List<Question> questions = questionRepository.getMyBookmarkedQuestionsByCategory(member, BookmarkStatus.Y, category);

        if(questions.isEmpty()){
            return new ArrayList<>();
        }

        return getQuestionDtosFromQuestions(questions, member);
    }

    public List<QuestionDto> getRandomQuestions(String categoryString, Member member) {
        try{
            Category.valueOf(categoryString.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new BizException(QuestionResponseType.INVALID_CATEGORY);
        }

        List<Question> questions = questionRepository.getRandom10QuestionsByCategory(categoryString.toUpperCase());

        if(questions.isEmpty()){
            return new ArrayList<>();
        }
        return getQuestionDtosFromQuestions(questions, member);
    }
}
