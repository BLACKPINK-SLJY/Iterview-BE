package server.api.iterview.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import server.api.iterview.domain.jwt.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
