package esy.app.info;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthRestController {

    @GetMapping("/")
    public ResponseEntity<String> ok() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok().build();
    }
}
