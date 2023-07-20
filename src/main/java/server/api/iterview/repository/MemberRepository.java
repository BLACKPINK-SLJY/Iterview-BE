package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.member.Member;
import server.api.iterview.dto.member.MemberInfoDto;

import java.util.Optional;

@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);

    @Query(value = "select new server.api.iterview.dto.member.MemberInfoDto(" +
            "m.id, m.account)" +
            "from Member m " +
            "where m.account = :account")
    Optional<MemberInfoDto> getMemberInfoDtoByAccount(@Param("account") String account);

    Boolean existsMemberByAccount(String account);
    Optional<Member> findByRefreshToken(String token);
}