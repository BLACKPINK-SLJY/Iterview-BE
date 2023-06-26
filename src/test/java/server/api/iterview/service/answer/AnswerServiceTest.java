package server.api.iterview.service.answer;


import org.junit.jupiter.api.Test;
import server.api.iterview.dto.transcription.*;

import java.util.ArrayList;
import java.util.List;

class AnswerServiceTest {
    @Test
    public void t(){
        TranscriptionResponseDTO data = getData();
        Boolean endFlag = true;
        String sentence = "";
        String startTime = "";
        String endTime = "";
        for(TranscriptionItemDTO item : data.getResults().getItems()){
            String content = "";
            for(TranscriptionItemAlternativesDTO alternativesDTO : item.getAlternatives()){
                content += alternativesDTO.getContent();
            }
            sentence += content + " ";

            if(endFlag){
                startTime = item.getStart_time();
                endFlag = false;
            }

            if(content.equals(".")){
                sentence = sentence.replace(" . ", ".");
                endFlag = true;
                // 저장코드
                System.out.println(startTime + " / " + endTime + " / " + sentence);
                // 저장 코드 끝
                sentence = "";
            }

            endTime = item.getEnd_time();
        }
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




}