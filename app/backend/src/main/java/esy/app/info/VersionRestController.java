package esy.app.info;

import esy.api.info.Version;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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

    @GetMapping(value = "/version")
    public Version version() {
        return repository.find();
    }

    @GetMapping(value = "/version.adoc", produces = "text/asciidoc")
    public String versionAdoc() {
        return "= Version\n\n" + repository.find() + "\n";
    }

    @GetMapping(value = "/version.html", produces = "text/html")
    public String versionHtml() {
        return "<span id=\"version\">" + repository.find() + "</span><br/>\n";
    }
}
