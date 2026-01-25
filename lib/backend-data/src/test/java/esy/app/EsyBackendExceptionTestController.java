package esy.app;

import esy.api.Version;
import esy.api.basis.Enum;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.rest.webmvc.support.ETag;
import org.springframework.data.rest.webmvc.support.ETagDoesntMatchException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class EsyBackendExceptionTestController {

    @GetMapping("/badJson")
    public ResponseEntity<Version> badJson(@RequestBody final Version body) {
        return ResponseEntity.ok(body);
    }

    @GetMapping("/accessDeniedException")
    public ResponseEntity<Void> accessDeniedException(@RequestBody final String cause) {
        throw new AccessDeniedException(cause);
    }

    @GetMapping("/badCredentialsException")
    public ResponseEntity<Void> badCredentialsException(@RequestBody final String cause) {
        throw new BadCredentialsException(cause);
    }

    @GetMapping("/badSqlGrammarException")
    public ResponseEntity<Void> badSqlGrammarException(@RequestBody final String cause) {
        throw new BadSqlGrammarException(cause, "select * from dual", new SQLException(cause));
    }

    @GetMapping("/concurrencyFailureException")
    public ResponseEntity<Void> concurrencyFailureException(@RequestBody final String cause) {
        throw new ConcurrencyFailureException(cause);
    }

    @GetMapping("/dataIntegrityViolationException")
    public ResponseEntity<Void> dataIntegrityViolationException(@RequestBody final String cause) {
        throw new DataIntegrityViolationException(cause);
    }

    @GetMapping("/dataRetrievalFailureException")
    public ResponseEntity<Void> dataRetrievalFailureException(@RequestBody final String cause) {
        throw new DataRetrievalFailureException(cause);
    }

    @GetMapping("/entityExistsException")
    public ResponseEntity<Void> entityExistsException(@RequestBody final String cause) {
        throw new EntityExistsException(cause);
    }

    @GetMapping("/entityNotFoundException")
    public ResponseEntity<Void> entityNotFoundException(@RequestBody final String cause) {
        throw new EntityNotFoundException(cause);
    }

    @GetMapping("/etagDoesntMatchException")
    public ResponseEntity<Void> etagDoesntMatchException(@RequestBody final String cause) {
        final var value = Enum.parseJson("{}");
        throw new ETagDoesntMatchException(value, ETag.NO_ETAG);
    }

    @GetMapping("/httpClientErrorException/{code}")
    public ResponseEntity<Void> httpClientErrorException(@PathVariable int code) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(code));
    }

    @GetMapping("/httpServerErrorException/{code}")
    public ResponseEntity<Void> httpServerErrorException(@PathVariable int code) {
        throw new HttpServerErrorException(HttpStatusCode.valueOf(code));
    }

    @GetMapping("/fileNotFoundException")
    public ResponseEntity<Void> fileNotFoundException(@RequestBody final String cause) {
        throw new UncheckedIOException(cause, new FileNotFoundException(cause));
    }

    @GetMapping("/illegalArgumentException")
    public ResponseEntity<Void> illegalArgumentException(@RequestBody final String cause) {
        throw new IllegalArgumentException(cause);
    }

    @GetMapping("/illegalStateException")
    public ResponseEntity<Void> illegalStateException(@RequestBody final String cause) {
        throw new IllegalStateException(cause);
    }

    @GetMapping("/missingResourceException")
    public ResponseEntity<Void> missingResourceException(@RequestBody final String cause) {
        throw new MissingResourceException(cause, "String", "1");
    }

    @GetMapping("/noSuchFileException")
    public ResponseEntity<Void> noSuchFileException(@RequestBody final String cause) {
        throw new UncheckedIOException(cause, new NoSuchFileException(cause));
    }

    @GetMapping("/noSuchElementException")
    public ResponseEntity<Void> noSuchElementException(@RequestBody final String cause) {
        throw new NoSuchElementException(cause);
    }

    @GetMapping("/numberFormatException")
    public ResponseEntity<Void> numberFormatException(@RequestBody final String cause) {
        throw new NumberFormatException(cause);
    }

    @GetMapping("/nullPointerException")
    public ResponseEntity<Void> nullPointerException(@RequestBody final String cause) {
        throw new NullPointerException(cause);
    }

    @GetMapping("/optimisticLockException")
    public ResponseEntity<Void> optimisticLockException(@RequestBody final String cause) {
        throw new OptimisticLockException(cause);
    }

    @GetMapping("/pessimisticLockException")
    public ResponseEntity<Void> pessimisticLockException(@RequestBody final String cause) {
        throw new PessimisticLockException(cause);
    }

    @GetMapping("/resourceAccessException")
    public ResponseEntity<Void> resourceAccessException(@RequestBody final String cause) {
        throw new ResourceAccessException(cause);
    }

    @GetMapping("/unsupportedEncodingException")
    public ResponseEntity<Void> unsupportedEncodingException(@RequestBody final String cause) {
        throw new UncheckedIOException(cause, new UnsupportedEncodingException(cause));
    }

    @GetMapping("/unsupportedOperationException")
    public ResponseEntity<Void> unsupportedOperationException(@RequestBody final String cause) {
        throw new UnsupportedOperationException(cause);
    }
}
