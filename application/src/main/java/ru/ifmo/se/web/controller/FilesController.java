package ru.ifmo.se.web.controller;

import com.hierynomus.smbj.share.File;
import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.service.api.FilesService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.UUID;

@Path("/files")
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class FilesController {

    @Context
    SecurityContext securityContext;

    @Inject
    FilesService filesService;

    @GET
    @Path("/dataset/{uuid}")
    @RolesAllowed(UserRolesConstants.USER)
    public Response downloadDataset(@PathParam("uuid") UUID datasetUuid) {
        return Response.ok().entity(
                        (StreamingOutput) output -> {
                            File file = filesService.getDatasetFile(securityContext.getUserPrincipal().getName(), datasetUuid);
                            InputStream datasetIS = file.getInputStream();
                            byte[] buf = new byte[8192];
                            int length;
                            while ((length = datasetIS.read(buf)) != -1) {
                                output.write(buf, 0, length);
                            }
                            output.flush();
                            datasetIS.close();
                            file.close();
                        })
                .header("Content-Disposition", "attachment;filename=" + "dataset.zip")
                .build();
    }
}
