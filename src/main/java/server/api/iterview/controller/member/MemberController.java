package server.api.iterview.controller.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.dto.jwt.TokenDto;
import server.api.iterview.dto.member.MemberInfoDto;
import server.api.iterview.dto.member.SignRequest;
import server.api.iterview.dto.member.SignResponse;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.BizException;
import server.api.iterview.response.jwt.JwtResponseType;
import server.api.iterview.response.member.MemberResponseType;
import server.api.iterview.service.member.MemberService;
import server.api.iterview.service.s3.AmazonS3Service;

@Api(tags = "Member")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입", notes = "회원가입")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20100, message = "회원가입 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40106, message = "이미 사용중인 닉네임입니다. (200)"),
            @io.swagger.annotations.ApiResponse(code = 40109, message = "회원가입에 실패하였습니다 (500)")
    })
    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody SignRequest request) {
        if(memberService.existsAccount(request.getAccount())){
            throw new BizException(MemberResponseType.DUPLICATE_NICKNAME);
        }

        memberService.signup(request);

        return ApiResponse.of(MemberResponseType.SIGNUP_SUCCESS);
    }

    @ApiOperation(value = "로그인", notes = "로그인")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20101, message = "로그인 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (400)"),
    })
    @PostMapping("/login")
    public ApiResponse<SignResponse> login(@RequestBody SignRequest request) {
        SignResponse response = memberService.login(request);

        return ApiResponse.of(MemberResponseType.LOGIN_SUCCESS, response);
    }

    @ApiOperation(value = "회원정보 조회", notes = "회원정보 조회")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20102, message = "회원정보 조회 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40003, message = "access token 만료 (400)"),
            @io.swagger.annotations.ApiResponse(code = 40008, message = "지원되지 않는 JWT 토큰입니다. (400)"),
            @io.swagger.annotations.ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
    })
    @GetMapping("/member/info")
    public ApiResponse<MemberInfoDto> getMemberInfo(@RequestHeader(name = "Authorization") String token){
        String account = memberService.getAccountByToken(token);
        MemberInfoDto memberInfoDto = memberService.getMemberInfo(account);

        return ApiResponse.of(MemberResponseType.MEMBER_INFO_GET_SUCCESS, memberInfoDto);
    }

    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20001, message = "토큰 재발급 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40004, message = "리프레시 토큰 만료 (400) - 재 로그인 필요"),
            @io.swagger.annotations.ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
    })
    @PostMapping("/refresh")
    public ApiResponse<TokenDto> refresh(@RequestBody TokenDto token) {
        TokenDto tokenDto = memberService.refreshAccessToken(token);

        return ApiResponse.of(JwtResponseType.TOKEN_REISSUED, tokenDto);
    }

    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20104, message = "회원탈퇴 성공 (200)"),
            @io.swagger.annotations.ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
            @io.swagger.annotations.ApiResponse(code = 40110, message = "회원탈퇴 실패 (500)"),
    })
    @GetMapping("/member/withdraw")
    public ApiResponse<String> withdraw(@RequestHeader("Authorization") String token){
        String account = memberService.getAccountByToken(token);
        memberService.withdraw(account);

        return ApiResponse.of(MemberResponseType.WITHDRAWAL_SUCCESS);
    }
}
