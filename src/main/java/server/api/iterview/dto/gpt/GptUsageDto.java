package server.api.iterview.dto.gpt;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptUsageDto {
    int prompt_tokens;
    int completion_tokens;
    int total_tokens;
}
