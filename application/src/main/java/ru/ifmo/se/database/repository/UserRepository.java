package ru.ifmo.se.database.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import ru.ifmo.se.database.model.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {
}
