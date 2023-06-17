package server.api.iterview.controller.transcription;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.transcription.TranscriptionResponseDTO;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.BizException;
import server.api.iterview.response.InternalServerExceptionType;
import server.api.iterview.response.transcribe.TranscribeResponseType;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.service.transcribe.TranscriptionService;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;

@Api(tags = "Transcription")
@RestController
@RequiredArgsConstructor
public class TranscriptionController {

    private final MemberService memberService;
    private final TranscriptionService transcriptionService;

    @GetMapping("/transcription")
    public ApiResponse<Object> createTranscription(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        TranscriptionResponseDTO dto = transcriptionService.extractSpeechTextFromVideo(member, questionId);

        return ApiResponse.of(TranscribeResponseType.TRANSCRIBE_OK, dto);
    }

    @ApiOperation(value = "더미 텍스트 데이터", notes = "더미 데이터")
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
            "    \"jobName\": \"9165f727-0e99-4d25-9a9a-5ebd4a0ea494\",\n" +
            "    \"accountId\": \"850775240476\",\n" +
            "    \"results\": {\n" +
            "      \"transcripts\": [\n" +
            "        {\n" +
            "          \"transcript\": \"동해물과 백두산이 마르고 닳도록 하느님이 보호사 우리나라 만세\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"start_time\": \"1.13\",\n" +
            "          \"end_time\": \"1.74\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9996\",\n" +
            "              \"content\": \"동해물과\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"1.74\",\n" +
            "          \"end_time\": \"2.31\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"1.0\",\n" +
            "              \"content\": \"백두산이\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.31\",\n" +
            "          \"end_time\": \"2.71\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"1.0\",\n" +
            "              \"content\": \"마르고\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"2.71\",\n" +
            "          \"end_time\": \"3.46\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"1.0\",\n" +
            "              \"content\": \"닳도록\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"3.49\",\n" +
            "          \"end_time\": \"4.24\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.993\",\n" +
            "              \"content\": \"하느님이\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"4.3\",\n" +
            "          \"end_time\": \"5.03\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.7063999999999999\",\n" +
            "              \"content\": \"보호사\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.06\",\n" +
            "          \"end_time\": \"5.47\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.9926\",\n" +
            "              \"content\": \"우리나라\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"start_time\": \"5.47\",\n" +
            "          \"end_time\": \"6.03\",\n" +
            "          \"alternatives\": [\n" +
            "            {\n" +
            "              \"confidence\": \"0.8766\",\n" +
            "              \"content\": \"만세\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"type\": \"pronunciation\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    \"status\": \"COMPLETED\"\n" +
            "  }";
}