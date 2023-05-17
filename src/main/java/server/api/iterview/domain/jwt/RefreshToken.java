package server.api.iterview.domain.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name="refresh_token")
@Entity
@Builder
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_id")
    private Long id;

    @Column(name = "rt_key")
    private String key;

    @Column(name = "rt_value")
    private String value;

    public void updateValue(String token){
        this.value = token;
    }
}
