package server.api.iterview.dto.gpt;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class GptResponseDto {
    String id;
    String object;
    String created;
    String model;
    GptUsageDto usage;
    List<GptChoiceDto> choices;
}
