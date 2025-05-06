package cc.suvankar.dictionaryapi.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EndpointsController {

    @GetMapping("/api/v1/endpoints")
    public List<String> getEndpoints() {
        return Arrays.asList(
                "/api/v1/parse?file={file}",
                "/api/v1/definitions?word={word}",
                "/api/v1/endpoints");
    }
}
