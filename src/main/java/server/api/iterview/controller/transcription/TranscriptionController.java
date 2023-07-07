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
import server.api.iterview.dto.transcription.TranscriptionResponseDTO;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.BizException;
import server.api.iterview.response.InternalServerExceptionType;
import server.api.iterview.response.transcribe.TranscribeResponseType;
import server.api.iterview.service.answer.AnswerService;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.service.transcribe.TranscriptionService;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;

@Api(tags = "Transcription")
@RestController
@RequiredArgsConstructor
public class TranscriptionController {

    private final MemberService memberService;
    private final AnswerService answerService;

    @ApiOperation(value = "영상에서 텍스트 추출", notes = "AWS Transcribe API를 이용하여 영상에서 텍스트 추출 작업 실행")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20402, message = "Speech Text 추출 진행 시작"),
            @io.swagger.annotations.ApiResponse(code = 40501, message = "해당 유저에 대한 답변 데이타가 없음 (404)"),
            @io.swagger.annotations.ApiResponse(code = 40401, message = "transcribe 실패 (500)"),
            @io.swagger.annotations.ApiResponse(code = 40402, message = "이미 텍스트 추출된 영상입니다 (200)"),
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
        }

        // 해당 함수 비동기
        answerService.extractTextAndSave(member, questionId, answer);

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
            Object jsonObject = objectMapper.readValue(dummyString, Object.class);
            return ApiResponse.of(TranscribeResponseType.TRANSCRIBE_OK, jsonObject);
        }catch (Exception e){
            throw new BizException(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
        }

    }

    String dummyString = "{\n" +
            "    \"category\": \"IOS\",\n" +
            "    \"date\": \"2023.06.27 화요일\",\n" +
            "    \"url\": \"https://iterview-bucket.s3.ap-northeast-2.amazonaws.com/9fb02c54-29a5-437e-aaf0-25f7f2ae1b58/5?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230626T170430Z&X-Amz-SignedHeaders=host&X-Amz-Expires=599&X-Amz-Credential=AKIA4MFRH54OCAMCQVXR%2F20230626%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=c7090a333f7ccdab2f81f91aa01674fa4d35d039befa429705af550e78a40df7\",\n" +
            "    \"length\": null,\n" +
            "    \"results\": {\n" +
            "      \"transcripts\": [\n" +
            "        {\n" +
            "          \"transcript\": \"자세히 보아야 예쁘다. 오래 보아야 사랑스럽다. 너도 그렇다. 스프링은 잡아언어를 기반으로 한다.\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"start_time\": \"0.4\",\n" +
            "          \"end_time\": \"1.68\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"자세히 보아야 예쁘다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.12\",\n" +
            "          \"end_time\": \"3.61\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"오래 보아야 사랑스럽다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.03\",\n" +
            "          \"end_time\": \"4.97\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"너도 그렇다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.37\",\n" +
            "          \"end_time\": \"7.75\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": null,\n" +
            "              \"content\": \"스프링은 잡아언어를 기반으로 한다.\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }";
}