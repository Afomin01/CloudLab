package ru.ifmo.se.web.controller;

import ru.ifmo.se.service.api.UserService;
import ru.ifmo.se.web.model.UserRegisterRequestDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    UserService userService;

    @Path("/register")
    @POST
    public void register(UserRegisterRequestDto requestDto) {
        userService.registerNewUser(requestDto);
    }
}
