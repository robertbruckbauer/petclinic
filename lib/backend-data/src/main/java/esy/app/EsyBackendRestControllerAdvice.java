package esy.app;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.support.ETagDoesntMatchException;
import org.springframework.http.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.NoSuchFileException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RestController
public class EsyBackendRestControllerAdvice extends ResponseEntityExceptionHandler implements ErrorController {

    /**
     * @see org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleAndThrow(final WebRequest request, final ResourceNotFoundException cause) {
        // Do NOT handle here
        throw cause;
    }

    @ExceptionHandler(ETagDoesntMatchException.class)
    public ResponseEntity<Object> handlePreconditionFailed(final WebRequest request, final ETagDoesntMatchException cause) {
        final var status = HttpStatus.PRECONDITION_FAILED;
        final var error = new ResponseStatusException(status, cause.getMessage(), cause);
        error.setDetail(String.format("%s is outdated. Please reload content.", cause.getBean()));
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Object> handleRestClientResponse(final WebRequest request, final RestClientResponseException cause) {
        final var status = cause.getStatusCode();
        final var error = new ResponseStatusException(status, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handle(final WebRequest request, final Exception cause) {
        final var status = resolveStatus(cause);
        final var error = new ResponseStatusException(status, cause.getMessage(), cause);
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    // tag::resolveStatus[]
    @SuppressWarnings("java:S3776") // allow complexity
    protected HttpStatus resolveStatus(final Exception cause) {
        if (cause instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (cause instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        }
        if (cause instanceof DataRetrievalFailureException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof ResourceAccessException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof MissingResourceException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof NoSuchFileException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof FileNotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (cause instanceof UncheckedIOException) {
            return resolveStatus(((UncheckedIOException) cause).getCause());
        }
        if (cause instanceof DataIntegrityViolationException) {
            return HttpStatus.CONFLICT;
        }
        if (cause instanceof ConcurrencyFailureException) {
            return HttpStatus.CONFLICT;
        }
        if (cause instanceof EntityExistsException) {
            return HttpStatus.CONFLICT;
        }
        if (cause instanceof OptimisticLockException) {
            return HttpStatus.CONFLICT;
        }
        if (cause instanceof PessimisticLockException) {
            return HttpStatus.CONFLICT;
        }
        if (cause instanceof InvalidDataAccessApiUsageException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (cause instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (cause instanceof IllegalStateException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (cause instanceof BadSqlGrammarException) {
            return HttpStatus.NOT_IMPLEMENTED;
        }
        if (cause instanceof UnsupportedEncodingException) {
            return HttpStatus.NOT_IMPLEMENTED;
        }
        if (cause instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    // end::resolveStatus[]

    /**
     * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
     */
    @RequestMapping("${server.error.path:/error}")
    public ResponseEntity<Object> error(final WebRequest request, final HttpServletResponse response) {
        final var error = new ResponseStatusException(resolveStatus(response), resolveCause(request));
        return handleErrorResponseException(error, error.getHeaders(), error.getStatusCode(), request);
    }

    protected String resolveCause(final WebRequest request) {
        final var cause = request.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);
        return cause != null ? cause.toString() : null;
    }

    protected HttpStatus resolveStatus(final HttpServletResponse response) {
        final var statusCode = response.getStatus();
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> createResponseEntity(final Object body, @NonNull final HttpHeaders headers, @NonNull final HttpStatusCode statusCode, @NonNull final WebRequest request) {
        log.error(request.getDescription(false));
        return ResponseEntity
                .status(statusCode)
                .headers(headers)
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
