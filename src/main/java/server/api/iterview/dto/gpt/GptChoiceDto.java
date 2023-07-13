package server.api.iterview.dto.gpt;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptChoiceDto {
    GptMessageDto message;
    String finish_reason;
    int index;
}
