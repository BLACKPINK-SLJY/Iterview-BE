package server.api.iterview.controller.answer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;
import server.api.iterview.dto.amazonS3.PresignedUrlResponseDto;
import server.api.iterview.dto.answer.AnswerReportResponseDto;
import server.api.iterview.dto.answer.AnswerVideoResponseDto;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.amazonS3.AmazonS3ResponseType;
import server.api.iterview.response.answer.AnswerResponseType;
import server.api.iterview.service.answer.AnswerService;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.service.question.QuestionService;
import server.api.iterview.service.s3.AmazonS3Service;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER;

@Api(tags = "Answer")
@RequiredArgsConstructor
@RestController
public class AnswerController {
    private final MemberService memberService;
    private final AmazonS3Service amazonS3Service;
    private final AnswerService answerService;
    private final QuestionService questionService;

    @ApiOperation(value = "답변 영상 업로드용 URL 발급", notes = "presigned-url을 통해 영상을 업로드 하고 성공(OK)를 받았으면, '/answer/sync-db' API 호출 해주세요.")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20301, message = "presigned-url 발급에 성공하였습니다. (200)"),
            @io.swagger.annotations.ApiResponse(code = 40301, message = "presigned-url 발급에 실패하였습니다. (500)"),
    })
    @GetMapping("/answer/presigned-url/upload")
    public ApiResponse<PresignedUrlResponseDto> getPresignedUrlForUpload(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        String preSignedUrl = amazonS3Service.getPresignedUrlForUpload(member, questionId);

        return ApiResponse.of(AmazonS3ResponseType.PRESIGNED_URL_ISSUANCE_SUCCESS, new PresignedUrlResponseDto(preSignedUrl));
    }

    @ApiOperation(value = "답변 내역 DB 동기화", notes = "presigned-url을 통해 영상 업로드를 마치면 호출해야 할 API.\n사용자의 답변 내역을 DB에도 저장합니다. ")
    @GetMapping("/answer/sync-db")
    public ApiResponse<Object> syncAnswerToDB(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);

        answerService.syncDB(member, questionService.findById(questionId));

        return ApiResponse.of(AnswerResponseType.SYNC_DB_AFTER_UPLOAD_VIDEO_OK);
    }

    @ApiOperation(value = "답변 영상의 URL 발급", notes = "video 태그의 src 값으로 들어갈 영상 url 발급")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20301, message = "presigned-url 발급에 성공하였습니다. (200)"),
            @io.swagger.annotations.ApiResponse(code = 40301, message = "presigned-url 발급에 실패하였습니다. (500)"),
    })
    @GetMapping("/answer/presigned-url")
    public ApiResponse<PresignedUrlResponseDto> getPresignedUrl(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        String preSignedUrl = amazonS3Service.getPresignedUrl(member, questionId);

        return ApiResponse.of(AmazonS3ResponseType.PRESIGNED_URL_ISSUANCE_SUCCESS, new PresignedUrlResponseDto(preSignedUrl));
    }

    @ApiOperation(value = "내 답변 보기", notes = "내 답변 보기에서 필요한 모든 정보")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20502, message = "내 답변 보기 응답 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40301, message = "presigned-url 발급에 실패하였습니다. (500)"),
    })
    @GetMapping("/answer")
    public ApiResponse<AnswerReportResponseDto> getMyAnswer(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        Answer answer = answerService.findAnswerByMemberAndQuestionId(member, questionId);
        Question question = questionService.findById(questionId);

        AnswerReportResponseDto response = answerService.getAnswerResponse(member, question, answer);

        return ApiResponse.of(AnswerResponseType.MY_ANSWER_SUCCESS, response);
    }
    
    @ApiOperation(value = "내 답변 보기 - 녹화 영상 보러가기", notes = "")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20502, message = "내 답변 보기 응답 성공 (200)"),
    })
    @GetMapping("/answer/replay")
    public ApiResponse<AnswerVideoResponseDto> replayAnswer(
            @Parameter(name = "Authorization", description = "Bearer {accessToken}", in = HEADER) @RequestHeader(name = "Authorization") String token,
            @RequestParam Long questionId
    ){
        Member member = memberService.getMemberByToken(token);
        Answer answer = answerService.findAnswerByMemberAndQuestionId(member, questionId);
        Question question = questionService.findById(questionId);

        String preSignedUrl = amazonS3Service.getPresignedUrl(member, questionId);

        AnswerVideoResponseDto response = answerService.getReplayAnswerResponse(member, question, answer, preSignedUrl);

        return ApiResponse.of(AnswerResponseType.MY_ANSWER_SUCCESS, response);
    }
}
