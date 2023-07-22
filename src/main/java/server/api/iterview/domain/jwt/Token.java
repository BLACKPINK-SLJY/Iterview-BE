package server.api.iterview.domain.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Getter
//@RedisHash("refreshToken")
@Builder @AllArgsConstructor @NoArgsConstructor
@Entity
public class Token {


    @Id
    @JsonIgnore
    @Column(name = "MEMBER_ID")
    private Long id;

    private String refresh_token;

//    @TimeToLive(unit = TimeUnit.SECONDS)
    private LocalDateTime expiration;

    public void refreshExpiration(Long seconds) {
        this.expiration = LocalDateTime.now().plusSeconds(seconds);
    }
}
