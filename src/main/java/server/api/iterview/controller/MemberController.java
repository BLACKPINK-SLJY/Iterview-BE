package server.api.iterview.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.api.iterview.dto.member.MemberIDPWDto;
import server.api.iterview.jwt.TokenProvider;
import server.api.iterview.response.ResponseMessage;
import server.api.iterview.response.foo.FooResponseType;
import server.api.iterview.response.member.MemberResponseType;
import server.api.iterview.service.MemberService;

import javax.validation.Valid;

@Api(tags = "Member")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider jwtTokenProvider;

    @ApiOperation(value = "회원가입", notes = "회원가입")
    @ApiResponses({
            @ApiResponse(code = 2022, message = "사용자 정보 조회 성공 (200)"),
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@Valid @RequestBody MemberIDPWDto memberDto) {
        memberService.signup(memberDto);

        return new ResponseEntity<>(ResponseMessage.create(MemberResponseType.SIGNUP_SUCCESS), MemberResponseType.SIGNUP_SUCCESS.getHttpStatus());

    }

    @ApiOperation(value = "로그인", notes = "로그인")
    @ApiResponses({
            @ApiResponse(code = 2022, message = "로그인 성공 (200)"),
    })
    @PostMapping("/login")
    public String login(@Valid @RequestBody MemberIDPWDto memberDto) {
        return memberService.login(memberDto);
    }

    @ApiOperation(value = "회원정보", notes = "회원정보")
    @ApiResponses({
            @ApiResponse(code = 2000, message = "회원정보 리턴 성공"),
    })
    @GetMapping("/info")
    public String getMemberInfo(@RequestHeader(name = "Authorization") String token){
        return "0";
    }
}
