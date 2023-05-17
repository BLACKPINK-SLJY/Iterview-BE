package server.api.iterview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.member.Authority;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.member.MemberAuth;
import server.api.iterview.dto.member.MemberIDPWDto;
import server.api.iterview.jwt.TokenProvider;
import server.api.iterview.repository.AuthorityRepository;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.member.MemberResponseType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider jwtTokenProvider;
    private final AuthorityRepository authorityRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void signup(MemberIDPWDto memberDto){
        if(memberRepository.existsMemberByName(memberDto.getName())){
            throw new BizException(MemberResponseType.DUPLICATE_NICKNAME);
        }

        // DB 에서 ROLE_USER를 찾아서 권한으로 추가한다.
        Authority authority = authorityRepository
                .findByAuthorityName(MemberAuth.ROLE_USER).orElseThrow(()->new BizException(MemberResponseType.NO_AUTHORIZATION));

        Set<Authority> authoritySet = new HashSet<>();
        authoritySet.add(authority);

        Member member = memberDto.toMember(passwordEncoder, authoritySet);
        log.debug("signup: {} - {}", member.getId(), member.getName());

        try{
            memberRepository.save(member);
        }catch (Exception e){
            throw new BizException(MemberResponseType.SIGNUP_FAILED);
        }
    }

    @Transactional
    public String login(MemberIDPWDto memberDto){
        return "0";
    }
}