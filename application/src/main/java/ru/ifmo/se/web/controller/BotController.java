package ru.ifmo.se.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.connector.authentication.AuthenticationException;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.schema.Activity;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.se.configuration.BotConfiguration;
import ru.ifmo.se.service.impl.AzureBot;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionException;

@Path("/bot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class BotController {

    @Inject
    BotConfiguration configuration;

    @Inject
    AzureBot bot;

    @POST
    public Object listen(Activity activity, @DefaultValue("") @HeaderParam("Authorization") String authHeader) {
        BotFrameworkHttpAdapter adapter = configuration.getAdapter();

        return adapter.processIncomingActivity(authHeader, activity, this.bot).handle((result, exception) -> {
            if (exception == null) {
                return result != null ? Response.status(result.getStatus()).entity(result.getBody()).build() : Response.status(Response.Status.ACCEPTED).build();
            } else {
                log.error("Exception handling message", exception);
                if (exception instanceof CompletionException) {
                    return exception.getCause() instanceof AuthenticationException ? Response.status(Response.Status.UNAUTHORIZED).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                } else {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        });
    }
}
