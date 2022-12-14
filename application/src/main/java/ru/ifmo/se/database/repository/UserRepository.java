package ru.ifmo.se.database.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.apache.commons.collections4.CollectionUtils;
import ru.ifmo.se.database.model.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {
    public UserEntity findByName(String name) {
        List<UserEntity> userEntities = list("username", name);

        if (CollectionUtils.isEmpty(userEntities)) {
            return null;
        } else return userEntities.get(0);
    }

    public UserEntity findByTelegramId(String telegramId) {
        List<UserEntity> userEntities = list("telegramId", telegramId);

        if (CollectionUtils.isEmpty(userEntities)) {
            return null;
        } else return userEntities.get(0);
    }

    public UserEntity findByBotToken(String botToken) {
        List<UserEntity> userEntities = list("botToken", botToken);

        if (CollectionUtils.isEmpty(userEntities)) {
            return null;
        } else return userEntities.get(0);
    }
}
