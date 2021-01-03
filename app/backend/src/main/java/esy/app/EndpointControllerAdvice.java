package esy.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@RestController
@Slf4j
public class EndpointControllerAdvice extends ResponseEntityExceptionHandler implements ErrorController {

    @ExceptionHandler({
            DataRetrievalFailureException.class,
            ResourceAccessException.class
    })
    public void handleNotFound(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString(), getRootCause(cause));
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.NOT_FOUND.value(), cause.getMessage());
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConcurrencyFailureException.class
    })
    public void handleConflict(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString(), getRootCause(cause));
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.CONFLICT.value(), cause.getMessage());
    }

    @ExceptionHandler({
            DataAccessException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public void handleBadRequest(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString(), getRootCause(cause));
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.BAD_REQUEST.value(), cause.getMessage());
    }

    @ExceptionHandler({
            NullPointerException.class,
            UnsupportedOperationException.class,
            BadSqlGrammarException.class
    })
    public void handleError(final Exception cause, final HttpServletResponse response) throws IOException {
        log.error(cause.toString(), getRootCause(cause));
        log.trace(cause.toString(), cause);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), cause.getMessage());
    }

    @RequestMapping("${server.error.path:/error}")
    public void error(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final Object cause = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (cause instanceof Exception) {
            throw (Exception) cause;
        }
        final Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        final int status = getStatus(request);
        final String error = uri + " failed with code " + status;
        log.error(error);
        response.sendError(status, error);
    }

    private int getStatus(final HttpServletRequest request) {
        final Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private Throwable getRootCause(final Throwable cause) {
        Objects.requireNonNull(cause);
        Throwable rootCause = cause;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
