package server.api.iterview.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignRequest {

    private String account;

    private String password;
}

