package server.api.iterview.controller.transcription;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.transcription.TranscriptionResponseDTO;
import server.api.iterview.response.ApiResponse;
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

}