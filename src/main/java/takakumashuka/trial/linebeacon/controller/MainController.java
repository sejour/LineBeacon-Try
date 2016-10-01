package takakumashuka.trial.linebeacon.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class MainController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello " + new Date().toString();
    }

}
