package ru.ifmo.se.web.controller;

import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.service.api.FilesService;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;

@Path("/files")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class FilesController {

    @Context
    SecurityContext securityContext;

    @Inject
    FilesService filesService;

    @GET
    @RolesAllowed(UserRolesConstants.USER)
    public Response downloadFile() throws URISyntaxException {
        File nf = new File(getClass().getClassLoader().getResource("123.jpg").toURI());
        Response.ResponseBuilder response = Response.ok((Object) nf);
        response.header("Content-Disposition", "attachment;filename=" + nf.getName());
        return response.build();
    }

    @GET
    @Path("/dataset/{uuid}")
    @RolesAllowed(UserRolesConstants.USER)
    public Response downloadDataset(@PathParam("uuid") UUID datasetUuid) {
        return Response.ok().entity(
                        (StreamingOutput) output -> {
                            InputStream datasetIS = filesService.getDatasetStream(securityContext.getUserPrincipal().getName(), datasetUuid);
                            byte[] buf = new byte[8192];
                            int length;
                            while ((length = datasetIS.read(buf)) != -1) {
                                output.write(buf, 0, length);
                            }
                            output.flush();
                        })
                .header("Content-Disposition", "attachment;filename=" + "dataset.zip")
                .build();
    }
}
