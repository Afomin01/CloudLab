package ru.ifmo.se.web.controller;

import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.web.model.GenerationTaskParameters1DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskParameters2DRequestDto;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Path("/generator")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GeneratorController {

    @Inject
    GenerationService generationService;

    @Context
    SecurityContext securityContext;

    @POST()
    @Path("/1D/testImage")
    @Produces("image/png")
    public Response createTestImage1D(GenerationTaskParameters1DRequestDto requestDto) throws IOException {
        BufferedImage bufferedImage = generationService.createTestImage1D(requestDto);

        return Response.ok().entity((StreamingOutput) output -> {
                    ImageIO.write(bufferedImage, "png", output);
                    output.flush();
                })
                .header("Content-Disposition", "attachment;filename=" + "test.png")
                .build();
    }

    @POST()
    @Path("/1D/generate")
    @Produces("image/png")
    public Response generateDataset2D(GenerationTaskParameters1DRequestDto requestDto) throws IOException {
        generationService.createGenerationTask1D(requestDto, securityContext.getUserPrincipal().getName());

        return Response.noContent().build();
    }

    @POST()
    @Path("/2D/testImage")
    @Produces("image/png")
    public Response createTestImage2D(GenerationTaskParameters2DRequestDto requestDto) throws IOException {
        BufferedImage bufferedImage = generationService.createTestImage2D(requestDto);

        return Response.ok().entity((StreamingOutput) output -> {
                    ImageIO.write(bufferedImage, "png", output);
                    output.flush();
                })
                .header("Content-Disposition", "attachment;filename=" + "test.png")
                .build();
    }

    @POST()
    @Path("/2D/generate")
    @Produces("image/png")
    public Response generateDataset2D(GenerationTaskParameters2DRequestDto requestDto) throws IOException {
        generationService.createGenerationTask2D(requestDto, securityContext.getUserPrincipal().getName());

        return Response.noContent().build();
    }
}
