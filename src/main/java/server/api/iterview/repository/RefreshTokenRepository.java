package server.api.iterview.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import server.api.iterview.domain.jwt.RefreshToken;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final EntityManager em;

    public void saveRefreshToken(RefreshToken refreshToken){
        em.persist(refreshToken);
    }

    public void deleteRefreshToken(RefreshToken refreshToken){
        em.remove(refreshToken);
    }

    public Optional<RefreshToken> findByKey(String key){
        try {
            RefreshToken refreshToken = em
                    .createQuery("select t from RefreshToken t where t.key = :key", RefreshToken.class)
                    .setParameter("key", key)
                    .getSingleResult();
            return Optional.of(refreshToken);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public boolean existsByKey(String key){
        try{
            RefreshToken refreshToken = em
                .createQuery("select t from RefreshToken t where t.key = :key", RefreshToken.class)
                .setParameter("key", key)
                .getSingleResult();

            return true;
        }catch(Exception e){
            return false;
        }
    }
}
