package server.api.iterview.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import server.api.iterview.domain.member.Authority;
import server.api.iterview.domain.member.Member;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberIDPWDto {
    @NotNull
    @Size(min = 3, max = 20)
    private String name;

    @NotNull
    @Size(min = 6, max = 300)
    private String password;

    public Member toMember(PasswordEncoder passwordEncoder, Set<Authority> authorities) {
        return Member.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .authorities(authorities)
                .build();
    }
}
