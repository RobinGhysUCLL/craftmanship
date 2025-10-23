package be.ucll.group8.craftmanshipgroep8;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/status")
public class StatusController {

    @GetMapping
    public Map<String, String> status() {
        return Map.of("message", "Craftmanship backend is running");
    }

}
