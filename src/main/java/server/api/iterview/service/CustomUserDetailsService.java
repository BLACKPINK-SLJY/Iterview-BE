package server.api.iterview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.member.Authority;
import server.api.iterview.domain.member.Member;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.member.MemberResponseType;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberId) throws BizException {
        return memberRepository.findById(Long.parseLong(memberId))
                .map(this::createUserDetails)
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member member) {

        // Collections<? extends GrantedAuthority>
        List<SimpleGrantedAuthority> authList = member.getAuthorities()
                .stream()
                .map(Authority::getAuthorityName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(
                member.getId().toString(),
                member.getName(),
                authList
        );
    }
}