package esy.app.info;

import esy.api.info.Version;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class VersionRestController {

    private static Logger LOG = LoggerFactory.getLogger(VersionRestController.class);

    private final VersionRepository repository;

    @ExceptionHandler
    void onMissingResourceException(final MissingResourceException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.NO_CONTENT.value(), cause.getMessage());
    }

    @ExceptionHandler
    void onNoSuchElementException(final NoSuchElementException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.BAD_REQUEST.value(), cause.getMessage());
    }

    @ExceptionHandler
    void onInputMismatchException(final InputMismatchException cause, final HttpServletResponse response) throws IOException {
        LOG.error(cause.getMessage());
        LOG.trace(cause.getMessage(), cause);
        response.sendError(HttpStatus.BAD_REQUEST.value(), cause.getMessage());
    }

    @GetMapping(
            value = "/version",
            produces = "application/json"
    )
    public ResponseEntity<Version> json() {
        final Version version = repository.find();
        return ResponseEntity.ok().body(version);
    }

    @GetMapping(
            value = {"/version", "version.adoc"},
            produces = "text/asciidoc;charset=UTF-8"
    )
    public ResponseEntity<String> adoc() {
        final Version version = repository.find();
        final String adoc = version + "\n";
        return ResponseEntity.ok().body(adoc);
    }

    @GetMapping(
            value = {"/version", "version.html"},
            produces = "text/html;charset=UTF-8"
    )
    public ResponseEntity<String> html() {
        final Version version = repository.find();
        final String html = "<span id=\"version\">" + version + "</span><br/>\n";
        return ResponseEntity.ok().body(html);
    }
}
