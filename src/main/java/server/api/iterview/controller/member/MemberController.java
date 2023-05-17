package server.api.iterview.controller.member;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.dto.jwt.TokenDto;
import server.api.iterview.dto.member.MemberInfoDto;
import server.api.iterview.dto.member.SignRequest;
import server.api.iterview.dto.member.SignResponse;
import server.api.iterview.response.ResponseMessage;
import server.api.iterview.response.jwt.JwtResponseType;
import server.api.iterview.response.member.MemberResponseType;
import server.api.iterview.service.member.MemberService;

@Api(tags = "Member")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "회원가입", notes = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 20100, message = "회원가입 성공 (204)"),
            @ApiResponse(code = 40106, message = "이미 사용중인 닉네임입니다. (204)"),
            @ApiResponse(code = 40109, message = "회원가입에 실패하였습니다 (500)")
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@RequestBody SignRequest request) {
        if(memberService.existsAccount(request.getAccount())){
            return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.DUPLICATE_NICKNAME), MemberResponseType.DUPLICATE_NICKNAME.getHttpStatus());
        }

        memberService.signup(request);

        return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.SIGNUP_SUCCESS), MemberResponseType.SIGNUP_SUCCESS.getHttpStatus());
    }

    @ApiOperation(value = "로그인", notes = "로그인")
    @ApiResponses({
            @ApiResponse(code = 20101, message = "로그인 성공 (204)"),
            @ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody SignRequest request) {
        SignResponse response = memberService.login(request);

        return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.LOGIN_SUCCESS, response), MemberResponseType.LOGIN_SUCCESS.getHttpStatus());
    }

    @ApiOperation(value = "회원정보 조회", notes = "회원정보 조회")
    @ApiResponses({
            @ApiResponse(code = 20102, message = "회원정보 조회 성공 (200)"),
            @ApiResponse(code = 40003, message = "access token 만료 (400)"),
            @ApiResponse(code = 40008, message = "지원되지 않는 JWT 토큰입니다. (400)"),
            @ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
    })
    @GetMapping("/member/info")
    public ResponseEntity<ResponseMessage> getMemberInfo(@RequestHeader(name = "Authorization") String token){
        String account = memberService.getAccountByToken(token);
        MemberInfoDto memberInfoDto = memberService.getMemberInfo(account);

        return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.MEMBER_INFO_GET_SUCCESS, memberInfoDto), MemberResponseType.MEMBER_INFO_GET_SUCCESS.getHttpStatus());
    }

    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급")
    @ApiResponses({
            @ApiResponse(code = 20001, message = "토큰 재발급 성공 (200)"),
            @ApiResponse(code = 40004, message = "리프레시 토큰 만료 (400) - 재 로그인 필요"),
            @ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseMessage> refresh(@RequestBody TokenDto token) {
        TokenDto tokenDto = memberService.refreshAccessToken(token);

        return new ResponseEntity<>(ResponseMessage.create(JwtResponseType.TOKEN_REISSUED, tokenDto), JwtResponseType.TOKEN_REISSUED.getHttpStatus());
    }

    @ApiOperation(value = "회원 탈퇴", notes = "회원 탈퇴")
    @ApiResponses({
            @ApiResponse(code = 20104, message = "회원탈퇴 성공 (204)"),
            @ApiResponse(code = 40101, message = "사용자를 찾을 수 없습니다. (404)"),
            @ApiResponse(code = 40110, message = "회원탈퇴 실패 (500)"),
    })
    @GetMapping("/member/withdraw")
    public ResponseEntity<ResponseMessage> withdraw(@RequestHeader("Authorization") String token){
        String account = memberService.getAccountByToken(token);
        memberService.withdraw(account);

        return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.WITHDRAWAL_SUCCESS), MemberResponseType.WITHDRAWAL_SUCCESS.getHttpStatus());
    }


//    @GetMapping("/user/get")
//    public ResponseEntity<SignResponse> getUser(@RequestHeader("Authorization") String token, @RequestParam String account) throws Exception {
//        return new ResponseEntity<>( memberService.getMember(account), HttpStatus.OK);
//    }
//
//    @GetMapping("/admin/get")
//    public ResponseEntity<SignResponse> getUserForAdmin(@RequestParam String account) throws Exception {
//        return new ResponseEntity<>( memberService.getMember(account), HttpStatus.OK);
//    }
}
