package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.api.iterview.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
    Boolean existsMemberByName(String name);
}
