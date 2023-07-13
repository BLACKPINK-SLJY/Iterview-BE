package server.api.iterview.dto.gpt;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GptRequestDto{
    String model;
    Double temperature;
    List<GptMessageRequestDto> messages;
}
