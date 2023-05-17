package server.api.iterview.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.jwt.TokenDto;
import server.api.iterview.response.BizException;
import server.api.iterview.response.jwt.JwtResponseType;
import server.api.iterview.response.member.MemberResponseType;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private final String SECRET;
    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expire-time}") Long accessTokenExpireTime,
            @Value("${jwt.refresh-token-expire-time}") Long refreshTokenExpiresTime
    ){
        this.SECRET = secret;
        this.ACCESS_TOKEN_EXPIRE_TIME = accessTokenExpireTime;
        this.REFRESH_TOKEN_EXPIRE_TIME = refreshTokenExpiresTime;
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authentication 객체의 권한정보를 이용해서 토큰을 생성하는 메소드
     */
    private String createToken(Member member, Authentication authentication, Long validityTime){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Long now = (new Date()).getTime();
        Date validity = new Date(now + validityTime);

        Claims claims = Jwts.claims().setSubject(member.getId().toString()); // JWT payload 에 저장되는 정보단위
        claims.put("name", member.getName()); // 정보는 key / value 쌍으로 저장
        claims.put(AUTHORITIES_KEY, authorities);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public String createAccessToken(Member member, Authentication authentication){
        return createToken(member, authentication, this.ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(Member member, Authentication authentication){
        return createToken(member, authentication, this.REFRESH_TOKEN_EXPIRE_TIME);
    }


    /**
     * Token에 담겨있는 정보를 이용해 Authentication 객체를 리턴하는 메소드
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null || !StringUtils.hasText(claims.get(AUTHORITIES_KEY).toString())) {
            throw new BizException(MemberResponseType.NO_AUTHORIZATION); // 유저에게 아무런 권한이 없습니다.
        }

        log.debug("claims.getAuth = {}",claims.get(AUTHORITIES_KEY));
        log.debug("claims.getSubject = {}",claims.getSubject());

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new CustomSocialIdAuthToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성 검증을 수행하는 메소드
     */
    public Integer validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 1;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            return -1;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            return -2;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            return -3;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            return -4;
        }
    }

    public TokenDto createTokenDto(String accessToken, String refreshToken){
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(BEARER_TYPE)
                .build();
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) { // 만료된 토큰이더라도 일단 파싱을 함
            throw new BizException(JwtResponseType.ACCESS_TOKEN_EXPIRED);
        }
    }

    /**
     * 토큰 값을 파싱하여 Claim에 담긴 MemberId 값을 가져오는 메소드
     */
    public String getMemberIdByToken(String token){
        return this.parseClaims(token).getSubject();
    }


}