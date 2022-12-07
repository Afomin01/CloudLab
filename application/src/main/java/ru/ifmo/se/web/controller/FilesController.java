package ru.ifmo.se.web.controller;

import ru.ifmo.se.configuration.UserRolesConstants;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;

@Path("/files")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class FilesController {
    @GET
    @RolesAllowed(UserRolesConstants.USER)
    public Response downloadFile() throws URISyntaxException {
        File nf = new File(getClass().getClassLoader().getResource("123.jpg").toURI());
        Response.ResponseBuilder response = Response.ok((Object) nf);
        response.header("Content-Disposition", "attachment;filename=" + nf.getName());
        return response.build();
    }
}
