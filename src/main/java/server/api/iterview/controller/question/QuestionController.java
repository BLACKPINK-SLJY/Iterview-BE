package server.api.iterview.controller.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.question.QuestionDto;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.BizException;
import server.api.iterview.response.member.MemberResponseType;
import server.api.iterview.response.question.QuestionResponseType;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.service.question.QuestionService;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

@Api(tags = "Question")
@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    private final MemberService memberService;
    
    @ApiOperation(value = "질문 저장", notes = "질문 저장")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20200, message = "질문 저장 완료 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40200, message = "카테고리 에러 (400)"),
    })
    @PutMapping("/question")
    public ApiResponse<QuestionDto> insertQuestion(
            @Parameter(name = "content", description = "질문", in = QUERY) @RequestParam String content,
            @Parameter(name = "category", description = "ios / aos / fe / be", in = QUERY) @RequestParam String category,
            @Parameter(name = "keywords", description = "ex) 수평, 수직, 단방향, 양방향", in = QUERY) @RequestParam String keywords,
            @Parameter(name = "tags", description = "알아서 파싱할테니 그냥 콤마 포함해서 넣어주심 됩니다. ex) 스프링, 웹", in = QUERY) @RequestParam String tags,
            @Parameter(name = "level", description = "0, 1, 2", in = QUERY) @RequestParam Integer level
    ){
        QuestionDto questionDto = questionService.insertTerm(content, category, keywords, tags, level);

        return ApiResponse.of(QuestionResponseType.INSERT_SUCCESS, questionDto);
    }
    
    @ApiOperation(value = "질문 삭제", notes = "질문 삭제")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20201, message = "질문 삭제 완료 (200)"),
    })
    @DeleteMapping("/question")
    public ApiResponse<String> deleteQuestion(
            @Parameter(name = "id", description = "질문 id (Question ID)", in = QUERY) @RequestParam Long id
    ){
        questionService.deleteQuestion(id);

        return ApiResponse.of(QuestionResponseType.DELETE_SUCCESS);
    }

    @ApiOperation(value = "질문 리스트", notes = "쿼리 없을 경우 전체질문 응답,\n  있을 경우 ios/aos/fe/be\n 로그인 안해도 요청 가능 \n 토큰 담으면 북마크 여부 제대로 리턴\n 비회원은 북마크 무조건 N")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20202, message = "질문 리스트 추출 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40200, message = "카테고리 쿼리가 유효하지 않음 (400)"),
    })
    @GetMapping("/question/list")
    public ApiResponse<List<QuestionDto>> getQuestionList(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(name = "category", description = "X or ios/aos/fe/be", in = QUERY) @RequestParam(required = false) String category
    ){
        Member member = (token != null) ? memberService.getMemberByToken(token) : null;

        List<QuestionDto> questionDtos = (category == null) ? questionService.getAllQuestion(member) : questionService.getQuestionsByCategory(category, member);

        return ApiResponse.of(QuestionResponseType.LIST_GET_SUCCESS, questionDtos);
    }

    @ApiOperation(value = "질문 리스트 난이도 순", notes = "질문 리스트 난이도 순")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20202, message = "질문 리스트 추출 성공 (200"),
    })
    @GetMapping("/question/list/order/level")
    public ApiResponse<List<QuestionDto>> getQuestionListOrderByLevel(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(name = "category", description = "X or ios/aos/fe/be", in = QUERY) @RequestParam(required = false) String category
    ){
        Member member = (token != null) ? memberService.getMemberByToken(token) : null;

        List<QuestionDto> questionDtos = (category == null) ? questionService.getAllQuestionsOrderByLevel(member) : questionService.getQuestionsByCategoryOrderByLevel(category, member);

        return ApiResponse.of(QuestionResponseType.LIST_GET_SUCCESS, questionDtos);
    }

    @ApiOperation(value = "질문 검색", notes = "질문 검색 - 검색어가 질문 혹은 태그에 포함\n 난이도 순, 인기 순\n 로그인 안해도 요청 가능\n 토큰 담으면 북마크 여부 제대로 리턴\n 비회원은 북마크 무조건 N")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20202, message = "질문 리스트 추출 성공 (200)"),
    })
    @GetMapping("/question/search/{word}")
    public ApiResponse<List<QuestionDto>> getSearchResults(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable("word") String word
    ){
        Member member = (token != null) ? memberService.getMemberByToken(token) : null;

        List<QuestionDto> questionDtos = questionService.getSearchResults(word, member);

        return ApiResponse.of(QuestionResponseType.LIST_GET_SUCCESS, questionDtos);
    }

    @ApiOperation(value = "질문 북마크", notes = "질분 북마크.\n token 값이 없으면 로그인한 사용자가 아닌걸로 간주, 401 UNAUTHORIZED 응답")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20203, message = "북마크 성공"),
            @io.swagger.annotations.ApiResponse(code = 40111, message = "로그인 하지 않았습니다."),
    })
    @PutMapping("/question/bookmark/{id}")
    public ApiResponse<Object> bookmarkQuestion(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(name = "id", description = "question id", in = PATH) @PathVariable("id") Long id
    ){
        if (token == null){
            throw new BizException(MemberResponseType.NOT_LOGGED_IN);
        }

        Member member = memberService.getMemberByToken(token);
        questionService.bookmarkQuestion(member, id);

        return ApiResponse.of(QuestionResponseType.BOOKMARK_SUCCESS);
    }

    @ApiOperation(value = "질문 북마크 취소", notes = "질분 북마크 취소.\n token 값이 없으면 로그인한 사용자가 아닌걸로 간주, 401 UNAUTHORIZED 응답")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20204, message = "북마크 성공"),
            @io.swagger.annotations.ApiResponse(code = 40111, message = "로그인 하지 않았습니다."),
    })
    @PutMapping("/question/unbookmark/{id}")
    public ApiResponse<Object> unbookmarkQuestion(
            @RequestHeader(value = "Authorization", required = false) String token,
            @Parameter(name = "id", description = "question id", in = PATH) @PathVariable("id") Long id
    ){
        if (token == null){
            throw new BizException(MemberResponseType.NOT_LOGGED_IN);
        }

        Member member = memberService.getMemberByToken(token);
        questionService.unbookmarkQuestion(member, id);

        return ApiResponse.of(QuestionResponseType.UNBOOKMARK_SUCCESS);
    }
}
