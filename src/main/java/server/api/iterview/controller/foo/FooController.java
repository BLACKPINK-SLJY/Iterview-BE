package server.api.iterview.controller.foo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.api.iterview.dto.foo.FooDto;
import server.api.iterview.dto.transcription.*;
import server.api.iterview.response.BizException;
import server.api.iterview.response.ApiResponse;
import server.api.iterview.response.foo.FooResponseType;
import server.api.iterview.service.foo.FooService;


import java.util.ArrayList;
import java.util.List;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

@Api(tags = "Foo")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1")
public class FooController {
    private final FooService fooService;
    
    @ApiOperation(value = "요청 예시", notes = "요청 예시입니다!\nname에 \"error\" : controller에서 error 처리하는 경우\ntitle에 \"error\" : service에서 error 처리하는 경우")
    @ApiResponses({
            @io.swagger.annotations.ApiResponse(code = 20001, message = "Foo 객체 정상 리턴 (200 OK)"),
            @io.swagger.annotations.ApiResponse(code = 40001, message = "parameter 누락 (400 BAD_REQUEST)")
    })
    @GetMapping("/foo")
    public ApiResponse<Object> getFoo(
            @Parameter(name = "name", description = "String", in = QUERY, required = false) @RequestParam(required = false) String name,
            @Parameter(name = "title", description = "String", in = QUERY, required = false) @RequestParam String title
    ){
        // 담아줄 데이터가 없으면 create() 함수의 두번째 인자는 쓰지 않아도 됩니다.
        // Exception 던지는 부분엔 log 출력 코드 넣기
        // 주석 처리한 return 문처럼 에러 응답해도 무관
        if (name == null || title == null || name.equals("error")){
//            return new ResponseEntity<>(ResponseMessage.create(FooResponseType.INVALID_PARAMETER), FooResponseType.INVALID_PARAMETER.getHttpStatus());
            log.error(FooResponseType.INVALID_PARAMETER.getMessage());
            throw new BizException(FooResponseType.INVALID_PARAMETER);
        }

        FooDto fooDto = fooService.getFoo(name, title);

        // exception 이외에도 log 넣고 싶으면 넣기
        log.info(FooResponseType.FOO_CREATE_SUCCESS.getMessage());

        // 응답해줄 데이터가 있으면 create() 함수의 두번째 인자에 Dto를 담아준다.
        return ApiResponse.of(FooResponseType.FOO_CREATE_SUCCESS, fooDto);
    }

    @GetMapping("/goo")
    public ApiResponse<Object> getGoo(){
        return ApiResponse.of(FooResponseType.FOO_CREATE_SUCCESS, getData());
    }

    public TranscriptionResponseDTO getData(){
        TranscriptionResponseDTO responseDTO = new TranscriptionResponseDTO();

        List<TranscriptionTextDTO> textDTOS = List.of(new TranscriptionTextDTO("자세히 보아야 예쁘다. 오래 보아야 사랑스럽다. 너도 그렇다. 스프링은 잡아언어를 기반으로 한다."));
        List<TranscriptionItemDTO> itemDTOS = new ArrayList<>();

        List<Double> startTimes = List.of(0.4, 0.84, 1.2, 0.0, 2.12, 2.37, 2.78, 0.0, 4.03, 4.41, 0.0, 5.37, 6.19, 6.94, 7.36, 0.0);
        List<Double> endTimes   = List.of(0.84, 1.2, 1.68, 0.0, 2.37, 2.78, 3.61, 0.0, 4.41, 4.97, 0.0, 6.07, 6.94, 7.36, 7.75, 0.0);
        List<String> contents = List.of("자세히", "보아야", "예쁘다", ".", "오래", "보아야", "사랑스럽다", ".", "너도", "그렇다", ".", "스프링은", "잡아언어를", "기반으로", "한다", ".");
        List<Double> confidences = List.of(1.0, 1.0, 1.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.9951, 0.0, 0.9859, 0.76635, 1.0, 0.9926, 0.0);

        for(int i=0; i<16; i++){
            List<TranscriptionItemAlternativesDTO> alternativesDTOS = new ArrayList<>();
            TranscriptionItemAlternativesDTO alternativesDTO = TranscriptionItemAlternativesDTO.builder()
                        .content(contents.get(i))
                        .confidence(confidences.get(i).toString())
                        .build();
            alternativesDTOS.add(alternativesDTO);


            TranscriptionItemDTO itemDTO = TranscriptionItemDTO.builder()
                    .start_time(startTimes.get(i).toString())
                    .end_time(endTimes.get(i).toString())
                    .alternatives(alternativesDTOS)
                    .build();

            itemDTOS.add(itemDTO);
        }

        TranscriptionResultDTO resultDTO = TranscriptionResultDTO.builder()
                .transcripts(textDTOS)
                .items(itemDTOS)
                .build();

        responseDTO.setResults(resultDTO);
        return responseDTO;
    }

    @GetMapping("/async")
    public String asyncTest(){
        fooService.testAsync();
        return "async 성공";
    }
}
