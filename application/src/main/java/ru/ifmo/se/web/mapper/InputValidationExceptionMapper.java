package ru.ifmo.se.web.mapper;

import ru.ifmo.se.exception.InputValidationException;
import ru.ifmo.se.web.model.ErrorDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;

@Provider
public class InputValidationExceptionMapper implements ExceptionMapper<InputValidationException> {
    @Override
    public Response toResponse(InputValidationException e) {
        var status = Response.Status.BAD_REQUEST;

        return Response
                .status(status)
                .entity(
                        ErrorDto
                        .builder()
                                .timestamp(Instant.now())
                                .code(status.getStatusCode())
                                .message(e.getMessage())
                        .build()
                )
                .build();
    }
}