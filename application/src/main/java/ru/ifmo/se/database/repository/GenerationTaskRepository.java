package ru.ifmo.se.database.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import ru.ifmo.se.database.model.GenerationStatus;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.model.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GenerationTaskRepository implements PanacheRepositoryBase<GenerationTaskEntity, UUID> {
    public List<GenerationTaskEntity> findCreatedTasks() {
        return list("status", GenerationStatus.CREATED);
    }

    public List<GenerationTaskEntity> findByUser(UserEntity user) {
        return list("user", user);
    }
}
