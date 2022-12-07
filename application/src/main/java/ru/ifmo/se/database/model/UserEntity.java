package ru.ifmo.se.database.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@Builder
@UserDefinition
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
public class UserEntity {
    @Id
    private UUID id;

    @Username
    private String username;
    @Password
    private String password;
    @Roles
    @Column(name = "role")
    private String role; // comma-separated list of roles

    @Column(name = "telegram_id")
    private String telegramId;

    private boolean blocked;
    private long quota;

    public void setPassword(String password) {
        this.password = BcryptUtil.bcryptHash(password);
    }
}
