package cc.suvankar.dictionaryapi.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EndpointsController {

    @GetMapping("/dictionaryapi/v1/endpoints")
    public List<String> getEndpoints() {
        return Arrays.asList(
                "/dictionaryapi/v1/parse?file={file}",
                "/dictionaryapi/v1/definitions?word={word}",
                "/dictionaryapi/v1/endpoints");
    }
}
