package ru.ifmo.se.web.controller;

import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.web.model.CreatedGenerationTaskResponseDto;
import ru.ifmo.se.web.model.GenerationTaskParameters1DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskParameters2DRequestDto;
import ru.ifmo.se.web.model.GenerationTaskResponseDto;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    @RolesAllowed(UserRolesConstants.USER)
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
    @RolesAllowed(UserRolesConstants.USER)
    public Response generateDataset2D(GenerationTaskParameters1DRequestDto requestDto) {
        UUID taskId = generationService.createGenerationTask1D(requestDto, securityContext.getUserPrincipal().getName());

        return Response
                .ok(CreatedGenerationTaskResponseDto.builder().taskId(taskId).build())
                .build();
    }

    @POST()
    @Path("/2D/testImage")
    @Produces("image/png")
    @RolesAllowed(UserRolesConstants.USER)
    public Response createTestImage2D(GenerationTaskParameters2DRequestDto requestDto) {
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
    @RolesAllowed(UserRolesConstants.USER)
    public Response generateDataset2D(GenerationTaskParameters2DRequestDto requestDto) throws IOException {
        UUID taskId =  generationService.createGenerationTask2D(requestDto, securityContext.getUserPrincipal().getName());

        return Response
                .ok(CreatedGenerationTaskResponseDto.builder().taskId(taskId).build())
                .build();
    }

    @GET
    @Path("/tasks")
    @RolesAllowed(UserRolesConstants.USER)
    public List<GenerationTaskResponseDto> getUserGenerationTasks() {
        return generationService.getUserGenerationTasks(securityContext.getUserPrincipal().getName());
    }
}
