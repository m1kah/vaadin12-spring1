package fi.mika.vaadin.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloResource {
    @RequestMapping(method = RequestMethod.GET, path =  "api/hello")
    String hello() {
        return "Hello, API!";
    }
}
