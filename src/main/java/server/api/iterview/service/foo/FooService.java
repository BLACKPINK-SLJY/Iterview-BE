package server.api.iterview.service.foo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.foo.Foo;
import server.api.iterview.dto.foo.FooDto;
import server.api.iterview.repository.FooRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.foo.FooResponseType;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FooService {
    private final FooRepository fooRepository;

    public FooDto getFoo(String name, String title){
        // service 에서도 다음과 같이 exception 을 던지면 알아서 response message를 만들어 클라이언트에게 응답해 줍니다.
        if (title.equals("error")){
            log.error(FooResponseType.INVALID_PARAMETER.getMessage());
            throw new BizException(FooResponseType.INVALID_PARAMETER);
        }

        // Invoke Exception in Optional Class
        Foo foo = fooRepository.findFoo(name, title)
                .orElseThrow(() -> new BizException(FooResponseType.INVALID_PARAMETER));

        return FooDto.builder()
                .name(foo.getName())
                .title(foo.getTitle())
                .build();
    }

    @Async
    public void testAsync() {
        try {
            System.out.println("testAsync() 함수 시작");
            Thread.sleep(5 * 1000);
            System.out.println("testAsync() 함수 진행 중");
            Thread.sleep(3 * 1000);
            System.out.println("testAsync() 함수 끝");
        }catch (Exception e){
            return;
        }
    }
}
