package server.api.iterview.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.jwt.Token;
import server.api.iterview.domain.member.Authority;
import server.api.iterview.dto.jwt.TokenDto;
import server.api.iterview.dto.member.MemberInfoDto;
import server.api.iterview.dto.member.SignRequest;
import server.api.iterview.dto.member.SignResponse;
import server.api.iterview.jwt.JwtProvider;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.repository.TokenRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.jwt.JwtResponseType;
import server.api.iterview.response.member.MemberResponseType;

import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignResponse login(SignRequest request) {
        Member member = memberRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BizException(MemberResponseType.NOT_FOUND_USER);
        }

        member.setRefreshToken(createRefreshToken(member));

        return SignResponse.builder()
                .account(member.getAccount())
//                .roles(member.getRoles())
                .token(TokenDto.builder()
                        .access_token(jwtProvider.createToken(member.getAccount(), member.getRoles()))
                        .refresh_token(member.getRefreshToken())
                        .build())
                .build();
    }

    @Transactional
    public boolean existsAccount(String account){
        return memberRepository.existsMemberByAccount(account);
    }
    @Transactional
    public boolean signup(SignRequest request) {
        if(existsAccount(request.getAccount())){
            throw new BizException(MemberResponseType.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .account(request.getAccount())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

        try {
            memberRepository.save(member);
        }catch (Exception e){
            throw new BizException(MemberResponseType.SIGNUP_FAILED);
        }
        return true;
    }

    @Transactional
    public Member getMember(String account) {
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        return member;
    }

    // Refresh Token ================

    /**
     * Refresh 토큰을 생성한다.
     * Redis 내부에는
     * refreshToken:memberId : tokenValue
     * 형태로 저장한다.
     */
    @Transactional
    public String createRefreshToken(Member member) {
        Token token = tokenRepository.save(
                Token.builder()
                        .id(member.getId())
                        .refresh_token(UUID.randomUUID().toString())
                        .expiration(120)
                        .build()
        );
        return token.getRefresh_token();
    }

    @Transactional
    public Token validRefreshToken(Member member, String refreshToken){
        Token token = tokenRepository.findById(member.getId())
                .orElseThrow(() -> new BizException(JwtResponseType.REFRESH_TOKEN_EXPIRED));

        // 해당유저의 Refresh 토큰 만료 : Redis에 해당 유저의 토큰이 존재하지 않음
        if (token.getRefresh_token() == null) {
            return null;
        } else {
            // 리프레시 토큰 만료일자가 얼마 남지 않았을 때 만료시간 연장..?
            if(token.getExpiration() < 10) {
                token.setExpiration(1000);
                tokenRepository.save(token);
            }

            // 토큰이 같은지 비교
            if(!token.getRefresh_token().equals(refreshToken)) {
                return null;
            } else {
                return token;
            }
        }
    }

    @Transactional
    public TokenDto refreshAccessToken(TokenDto token){
        String account = jwtProvider.getAccount(token.getAccess_token());
        Member member = memberRepository.findByAccount(account).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));
        Token refreshToken = validRefreshToken(member, token.getRefresh_token());

        if (refreshToken != null) {
            return TokenDto.builder()
                    .access_token(jwtProvider.createToken(account, member.getRoles()))
                    .refresh_token(refreshToken.getRefresh_token())
                    .build();
        } else {
            throw new BizException(MemberResponseType.LOGOUT_MEMBER);
        }
    }

    @Transactional
    public MemberInfoDto getMemberInfo(String account) {
        MemberInfoDto memberInfoDto = memberRepository.getMemberInfoDtoByAccount(account)
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        return memberInfoDto;
    }

    @Transactional
    public String getAccountByToken(String token){
        // Bearer 검증
        if (!token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
            throw new BizException(JwtResponseType.NO_BEARER);
        }else {
            token = token.split(" ")[1].trim();
            return jwtProvider.getAccount(token);
        }
    }

    @Transactional
    public void withdraw(String account) {
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        try {
            memberRepository.delete(member);
        }catch (Exception e){
            throw new BizException(MemberResponseType.WITHDRAWAL_FAILED);
        }
    }
}