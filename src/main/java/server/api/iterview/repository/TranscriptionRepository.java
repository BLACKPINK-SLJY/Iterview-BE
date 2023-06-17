package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.transcription.Transcription;

import java.util.List;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
    List<Transcription> findByAnswer(Answer answer);
}
