package ru.ifmo.se.web.mapper;

import io.vertx.mutiny.core.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.se.web.model.ErrorDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
@Slf4j
public class ThrowableMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        log.error(exception.getMessage(), exception);
        var status = Response.Status.INTERNAL_SERVER_ERROR;

        return Response
                .status(status)
                .entity(
                        ErrorDto
                                .builder()
                                .timestamp(Instant.now())
                                .code(status.getStatusCode())
                                .message("Unexpected server error.")
                                .build()
                )
                .header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON)
                .build();
    }
}
