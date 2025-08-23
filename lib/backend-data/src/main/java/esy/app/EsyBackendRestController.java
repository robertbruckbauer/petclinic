package esy.app;

import esy.EsyBackendAware;
import esy.api.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EsyBackendRestController implements EsyBackendAware {

    @GetMapping(ROOT_PATH)
    public ResponseEntity<String> root() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping(HEALTHZ_PATH)
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(HOME_PATH)
    public ResponseEntity<String> home() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).build();
    }

    static Version version() {
        final var resource = new ClassPathResource("VERSION");
        return Version.fromResource(resource);
    }

    @GetMapping(
            value = VERSION_PATH,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Version> versionJson() {
        final var version = version();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(version);
    }

    @GetMapping(
            value = {VERSION_PATH, VERSION_PATH + ".adoc"},
            produces = "text/asciidoc;charset=UTF-8"
    )
    public ResponseEntity<String> versionAdoc() {
        final var version = version();
        final var adoc = "%s\n".formatted(version);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(adoc);
    }

    @GetMapping(
            value = {VERSION_PATH, VERSION_PATH + ".html"},
            produces = "text/html;charset=UTF-8"
    )
    public ResponseEntity<String> versionHtml() {
        final var version = version();
        final var html = "<span id=\"version\">%s</span><br/>\n".formatted(version);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(html);
    }
}
