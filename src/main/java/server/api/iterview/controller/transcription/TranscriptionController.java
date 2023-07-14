package server.api.iterview.controller.transcription;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.answer.TranscriptStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.BizException;
import server.api.iterview.response.InternalServerExceptionType;
import server.api.iterview.response.transcribe.TranscribeResponseType;
import server.api.iterview.service.answer.AnswerService;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.vo.DummyResponseDataVO;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;

@Api(tags = "Transcription")
@RestController
@RequiredArgsConstructor
public class TranscriptionController {

    private final MemberService memberService;
    private final AnswerService answerService;

    @ApiOperation(value = "영상에서 텍스트 추출", notes = "AWS Transcribe API를 이용하여 영상에서 텍스트 추출 작업 실행, 그리고 Open-AI API 요청을 통해 점수, 피드백, 모범답안까지 요청")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20402, message = "Speech Text 추출 진행 시작"),
            @io.swagger.annotations.ApiResponse(code = 40501, message = "해당 유저에 대한 답변 데이타가 없음 (404)"),
            @io.swagger.annotations.ApiResponse(code = 40401, message = "transcribe 실패 (500)"),
            @io.swagger.annotations.ApiResponse(code = 40402, message = "이미 텍스트 추출된 영상입니다 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40403, message = "텍스트 추출이 아직 진행 중입니다. (200)"),
    })
    @GetMapping("/transcription")
    public ApiResponse<Object> createTranscription(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        Answer answer = answerService.findAnswerByMemberAndQuestionId(member, questionId);

        if(answer.getTranscriptStatus() == TranscriptStatus.Y){
            return ApiResponse.of(TranscribeResponseType.ALREADY_TRANSCRIBED);
        }else if(answer.getTranscriptStatus() == TranscriptStatus.ING){
            return ApiResponse.of(TranscribeResponseType.ING_TRANSCRIBE);
        }

        answerService.updateTranscriptStatusToING(answer);
        // 해당 함수 비동기
        // STT 추출 후 Open-AI API 요청까지.
        answerService.extractTextAndSaveAndRequestOpenAI(member, questionId, answer);

        return ApiResponse.of(TranscribeResponseType.TRANSCRIBE_ING);
    }


    @ApiOperation(value = "더미 데이터 - 내 답변 보기", notes = "더미 데이터")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20201, message = "질문 삭제 완료 (200)"),
    })
    @GetMapping("/transcription/dummy")
    public ApiResponse<Object> getDummyText(){
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Object jsonObject = objectMapper.readValue(DummyResponseDataVO.TRANSCRIPTION_DUMMY, Object.class);
            return ApiResponse.of(TranscribeResponseType.TRANSCRIBE_OK, jsonObject);
        }catch (Exception e){
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }

    }

}