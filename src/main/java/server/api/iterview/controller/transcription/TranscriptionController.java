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
    private final TranscriptionService transcriptionService;
    private final AnswerService answerService;

    @ApiOperation(value = "영상에서 텍스트 추출", notes = "AWS Transcribe API를 이용하여 영상에서 텍스트 추출 작업 실행")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20401, message = "transcribe 성공 (200)"),
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

        TranscriptionResponseDTO transcriptionResponse = transcriptionService.extractSpeechTextFromVideo(member, questionId);
        answerService.saveTranscription(transcriptionResponse, answer);

        return ApiResponse.of(TranscribeResponseType.TRANSCRIBE_OK, transcriptionResponse);
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

    String dummyString = " {\n" +
            "    \"category\": \"IOS\",\n" +
            "    \"date\": \"2023.06.18 일요일\",\n" +
            "    \"url\": \"https://iterview-bucket.s3.ap-northeast-2.amazonaws.com/9fb02c54-29a5-437e-aaf0-25f7f2ae1b58/4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230617T175600Z&X-Amz-SignedHeaders=host&X-Amz-Expires=599&X-Amz-Credential=AKIA4MFRH54OCAMCQVXR%2F20230617%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=f2b334683e814e25149291fba4d3132dc8a6700ad54e5561eab49e0551fc36fe\",\n" +
            "    \"length\": null,\n" +
            "    \"results\": {\n" +
            "      \"transcripts\": [\n" +
            "        {\n" +
            "          \"transcript\": \"포스트는 클라이언트에서 어버려 리소스를 생성 하거나 업데이트하기 위해 데이터를 보낼 때 사용되는 매소두다.\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"start_time\": \"0.06\",\n" +
            "          \"end_time\": \"0.79\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.944\",\n" +
            "              \"content\": \"포스트는\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"0.79\",\n" +
            "          \"end_time\": \"1.51\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9943\",\n" +
            "              \"content\": \"클라이언트에서\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"1.51\",\n" +
            "          \"end_time\": \"2.05\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.7460500000000001\",\n" +
            "              \"content\": \"어버려\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.08\",\n" +
            "          \"end_time\": \"2.78\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9835\",\n" +
            "              \"content\": \"리소스를\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.78\",\n" +
            "          \"end_time\": \"3.14\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.7087\",\n" +
            "              \"content\": \"생성\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"3.14\",\n" +
            "          \"end_time\": \"3.7\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"1.0\",\n" +
            "              \"content\": \"하거나\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"3.73\",\n" +
            "          \"end_time\": \"4.49\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.85145\",\n" +
            "              \"content\": \"업데이트하기\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.49\",\n" +
            "          \"end_time\": \"4.93\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9798\",\n" +
            "              \"content\": \"위해\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.94\",\n" +
            "          \"end_time\": \"5.49\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9997\",\n" +
            "              \"content\": \"데이터를\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.49\",\n" +
            "          \"end_time\": \"5.8\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.987\",\n" +
            "              \"content\": \"보낼\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.8\",\n" +
            "          \"end_time\": \"5.94\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9804\",\n" +
            "              \"content\": \"때\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.94\",\n" +
            "          \"end_time\": \"6.45\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"1.0\",\n" +
            "              \"content\": \"사용되는\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"6.45\",\n" +
            "          \"end_time\": \"7.05\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.730675\",\n" +
            "              \"content\": \"매소두다\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": null,\n" +
            "          \"end_time\": null,\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.0\",\n" +
            "              \"content\": \".\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": null\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }";
}