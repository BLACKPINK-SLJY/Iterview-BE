package server.api.iterview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {
    @GetMapping("/")
    public ResponseEntity<Object> base(){
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
