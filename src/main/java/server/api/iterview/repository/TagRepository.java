package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.question.Tag;

import java.util.Optional;

@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
}
