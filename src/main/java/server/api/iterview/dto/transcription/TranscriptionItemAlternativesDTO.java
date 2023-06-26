package server.api.iterview.dto.transcription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionItemAlternativesDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String confidence;
    private String content;

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        TranscriptionItemAlternativesDTO transcriptionItemAlternativesDTO = (TranscriptionItemAlternativesDTO) o;
        if (transcriptionItemAlternativesDTO.getContent() == null){
            return false;
        }
        return Objects.equals(getContent(), transcriptionItemAlternativesDTO.getContent());
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(getContent());
    }

    @Override
    public String toString(){
        return "TranscriptionItemAlternativesDTO{" +
                "content=" + getContent() +
                ", confidence='" + getConfidence() + "'" +
                "}";
    }
}
