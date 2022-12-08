package ru.ifmo.se.web.controller;

import ru.ifmo.se.configuration.UserRolesConstants;
import ru.ifmo.se.service.api.AdminService;
import ru.ifmo.se.service.api.GenerationService;
import ru.ifmo.se.web.model.admin.DeleteTaskResultRequestDto;
import ru.ifmo.se.web.model.admin.UserBlockRequestDto;
import ru.ifmo.se.web.model.admin.UserChangeQuotaRequestDto;
import ru.ifmo.se.web.model.admin.UserRegisterRequestDto;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminController {

    @Inject
    AdminService adminService;

    @Inject
    GenerationService generationService;

    @Path("/register")
    @POST
    @RolesAllowed(UserRolesConstants.ADMIN)
    public void register(UserRegisterRequestDto requestDto) {
        adminService.registerNewUser(requestDto);
    }

    @Path("/block")
    @POST
    @RolesAllowed(UserRolesConstants.ADMIN)
    public void block(UserBlockRequestDto requestDto) {
        adminService.blockUser(requestDto);
    }

    @Path("/unblock")
    @POST
    @RolesAllowed(UserRolesConstants.ADMIN)
    public void unblock(UserBlockRequestDto requestDto) {
        adminService.unblockUser(requestDto);
    }

    @Path("/change-quota")
    @POST
    @RolesAllowed(UserRolesConstants.ADMIN)
    public void changeQuota(UserChangeQuotaRequestDto requestDto) {
        adminService.changeQuota(requestDto);
    }

    @Path("/delete-task-result")
    @POST
    @RolesAllowed(UserRolesConstants.ADMIN)
    public void deleteTaskResult(DeleteTaskResultRequestDto requestDto) {
        generationService.deleteTaskResult(requestDto, requestDto.getUsername());
    }
}
