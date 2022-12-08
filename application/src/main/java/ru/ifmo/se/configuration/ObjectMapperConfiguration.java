package ru.ifmo.se.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import io.quarkus.runtime.Startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Startup
public class ObjectMapperConfiguration {
    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        objectMapper.registerModule(new JodaModule());
    }
}
