package server.api.iterview.controller.question;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.question.QuestionDto;
import server.api.iterview.response.ResponseMessage;
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
            @ApiResponse(code = 20200, message = "질문 저장 완료 (200)"),
            @ApiResponse(code = 40200, message = "카테고리 에러 (400)"),
    })
    @PutMapping("/question")
    public ResponseEntity<ResponseMessage<QuestionDto>> insertQuestion(
            @Parameter(name = "content", description = "질문", in = QUERY) @RequestParam String content,
            @Parameter(name = "category", description = "ios / aos / fe / be", in = QUERY) @RequestParam String category,
            @Parameter(name = "keywords", description = "ex) 수평, 수직, 단방향, 양방향", in = QUERY) @RequestParam String keywords,
            @Parameter(name = "tags", description = "알아서 파싱할테니 그냥 콤마 포함해서 넣어주심 됩니다. ex) 스프링, 웹", in = QUERY) @RequestParam String tags,
            @Parameter(name = "level", description = "0, 1, 2", in = QUERY) @RequestParam Integer level
    ){
        QuestionDto questionDto = questionService.insertTerm(content, category, keywords, tags, level);

        return new ResponseEntity<>(ResponseMessage.create(QuestionResponseType.INSERT_SUCCESS, questionDto), QuestionResponseType.INSERT_SUCCESS.getHttpStatus());
    }
    
    @ApiOperation(value = "질문 삭제", notes = "질문 삭제")
    @ApiResponses({
            @ApiResponse(code = 20201, message = "질문 삭제 완료 (200)"),
    })
    @DeleteMapping("/question")
    public ResponseEntity<ResponseMessage> deleteQuestion(
            @Parameter(name = "id", description = "질문 id (Question ID)", in = QUERY) @RequestParam Long id){
        questionService.deleteQuestion(id);

        return new ResponseEntity<>(ResponseMessage.create(QuestionResponseType.DELETE_SUCCESS), QuestionResponseType.DELETE_SUCCESS.getHttpStatus());
    }

    @ApiOperation(value = "질문 리스트", notes = "쿼리 없을 경우 전체질문 응답,\n  있을 경우 ios/aos/fe/be")
    @ApiResponses({
            @ApiResponse(code = 20202, message = "질문 리스트 추출 성공 (200)"),
            @ApiResponse(code = 40200, message = "카테고리 쿼리가 유효하지 않음 (400)"),
    })
    @GetMapping("/question/list")
    public ResponseEntity<ResponseMessage<List<QuestionDto>>> getQuestionList(
            @RequestHeader("Authorization") String token,
            @Parameter(name = "category", description = "X or ios/aos/fe/be", in = QUERY) @RequestParam(required = false) String category
    ){
        Member member = memberService.getMemberByToken(token);
        List<QuestionDto> questionDtos = (category == null) ? questionService.getAllQuestion(member) : questionService.getQuestionsByCategory(category, member);

        return new ResponseEntity<>(ResponseMessage.create(QuestionResponseType.LIST_GET_SUCCESS, questionDtos), QuestionResponseType.LIST_GET_SUCCESS.getHttpStatus());
    }
}
