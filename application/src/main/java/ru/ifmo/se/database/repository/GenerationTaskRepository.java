package ru.ifmo.se.database.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import ru.ifmo.se.database.model.GenerationTaskEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class GenerationTaskRepository implements PanacheRepositoryBase<GenerationTaskEntity, UUID> {
}
