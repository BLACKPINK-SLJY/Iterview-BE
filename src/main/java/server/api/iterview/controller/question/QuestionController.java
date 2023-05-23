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
import server.api.iterview.dto.question.QuestionDto;
import server.api.iterview.dto.question.QuestionListDto;
import server.api.iterview.response.ResponseMessage;
import server.api.iterview.response.question.QuestionResponseType;
import server.api.iterview.service.question.QuestionService;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

@Api(tags = "Question")
@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final QuestionService questionService;
    
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

    @GetMapping("/question/list")
    public ResponseEntity<ResponseMessage<QuestionListDto>> getQuestionList(@RequestParam(required = false) String category){
        QuestionListDto questionListDto = (category == null) ? questionService.getAllQuestion() : questionService.getQuestionList(category);

        return new ResponseEntity<>(ResponseMessage.create(QuestionResponseType.LIST_GET_SUCCESS, questionListDto), QuestionResponseType.LIST_GET_SUCCESS.getHttpStatus());
    }
}
