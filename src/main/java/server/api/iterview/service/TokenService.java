package server.api.iterview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import server.api.iterview.domain.member.Authority;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.jwt.RefreshToken;
import server.api.iterview.domain.member.MemberAuth;
import server.api.iterview.dto.jwt.TokenDto;
import server.api.iterview.jwt.CustomSocialIdAuthToken;
import server.api.iterview.jwt.TokenProvider;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.repository.RefreshTokenRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.jwt.JwtResponseType;
import server.api.iterview.response.member.MemberResponseType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    public static final String BEARER_PREFIX = "Bearer ";

    @Transactional
    public TokenDto createToken(Member member){
        String memberId = member.getId().toString();

        CustomSocialIdAuthToken authenticationToken
                = new CustomSocialIdAuthToken(memberId, member.getName());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(member, authentication);

        // 만약 해당 유저의 refreshToken이 이미 있다면 삭제하고 재생성
        if(refreshTokenRepository.existsByKey(memberId)){
            refreshTokenRepository.deleteRefreshToken(refreshTokenRepository.findByKey(memberId).orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER)));
        }

        String refreshToken = tokenProvider.createRefreshToken(member, authentication);
        refreshTokenRepository.saveRefreshToken(
                RefreshToken.builder()
                        .key(memberId)
                        .value(refreshToken)
                        .build()
        );

        return tokenProvider.createTokenDto(accessToken, refreshToken);
    }

    @Transactional
    public TokenDto reissue(String rfToken) {
        String refreshToken = resolveToken(rfToken);

        Integer refreshTokenFlag = tokenProvider.validateToken(refreshToken);

        // refreshToken을 검증하고 상황에 맞는 오류를 내보낸다.
        if(refreshTokenFlag == -1){
            throw new BizException(JwtResponseType.MALFORMED_JWT);
        }else if(refreshTokenFlag == -2){
            throw new BizException(JwtResponseType.REFRESH_TOKEN_EXPIRED);
        }else if(refreshTokenFlag == -3){
            throw new BizException(JwtResponseType.UNSUPPORTED_JWT);
        }else if(refreshTokenFlag == -4){
            throw new BizException(JwtResponseType.BAD_TOKEN);
        }

        // refreshToken에서 memberId 가져오기
        String memberId = tokenProvider.getMemberIdByToken(refreshToken);

        // repo 에서 id를 기반으로 refresh token을 가져온다.
        RefreshToken originRefreshToken = refreshTokenRepository.findByKey(memberId)
                .orElseThrow(() -> new BizException(MemberResponseType.LOGOUT_MEMBER));

        // Refresh token 일치하는지 검사
        if(!originRefreshToken.getValue().equals(refreshToken)){
            throw new BizException(JwtResponseType.BAD_TOKEN); // 토큰이 일치하지 않습니다.
        }

        // 새로운 토큰 생성
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        TokenDto tokenDto = createToken(member);
        originRefreshToken.updateValue(tokenDto.getRefreshToken());

        return tokenDto;
    }

    private String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }

        return null;
    }

    public Member getMemberByToken(String token){
        String accessToken = resolveToken(token);
        String memberId = tokenProvider.getMemberIdByToken(accessToken);

        return memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));
    }

}
