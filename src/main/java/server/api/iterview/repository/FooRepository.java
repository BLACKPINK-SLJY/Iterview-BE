package server.api.iterview.repository;

import org.springframework.stereotype.Repository;
import server.api.iterview.domain.foo.Foo;


import java.util.Optional;

@Repository
public class FooRepository {

    public Optional<Foo> findFoo(String name, String title){
        Foo foo = new Foo(name, title);

        return Optional.of(foo);
    }

}
