package server.api.iterview.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.jwt.TokenDto;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SignResponse {
    private String account;

//    private List<Authority> roles = new ArrayList<>();

    private TokenDto token;

    public SignResponse(Member member) {
        this.account = member.getAccount();
//        this.roles = member.getRoles();
    }
}
