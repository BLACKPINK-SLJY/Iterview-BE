package server.api.iterview.dto.gpt;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GptMessageDto{
    String role;
    GptContentDto content;
}
