package server.api.iterview.dto.jwt;

import lombok.*;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class TokenDto {
    private String access_token;
    private String refresh_token;
}
