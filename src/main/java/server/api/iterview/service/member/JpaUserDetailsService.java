package server.api.iterview.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import server.api.iterview.domain.member.CustomUserDetails;
import server.api.iterview.domain.member.Member;
import server.api.iterview.repository.MemberRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.member.MemberResponseType;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByAccount(username)
                .orElseThrow(() -> new BizException(MemberResponseType.NOT_FOUND_USER));

        return new CustomUserDetails(member);
    }
}
