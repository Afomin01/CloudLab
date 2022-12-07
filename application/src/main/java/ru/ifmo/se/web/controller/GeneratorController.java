package ru.ifmo.se.web.controller;

import io.quarkus.runtime.graal.AwtImageIO;
import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.web.model.GenerationParametersRequestDto;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Path("/generator")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeneratorController {

    @Inject
    GenerationService generationService;

    @POST
    @Produces("image/png")
    public Response createGenerationTask(GenerationParametersRequestDto requestDto) throws IOException {
        BufferedImage bufferedImage = generationService.createGenerationTask(requestDto);

/*        File file = new File("tmp.png");
        ImageIO.write(bufferedImage, "png", file);

        Response.ResponseBuilder response = Response.ok(file);
        response.header("Content-Disposition", "attachment;filename=" + "test.png");*/
        return Response.ok().entity((StreamingOutput) output -> {
                    ImageIO.write(bufferedImage, "png", output);
                    output.flush();
                })
                .header("Content-Disposition", "attachment;filename=" + "test.png")
                .build();
    }
}
