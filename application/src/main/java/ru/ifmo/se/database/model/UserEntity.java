package ru.ifmo.se.database.model;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;
    private String password;

    @Column(name = "telegram_id")
    private String telegramId;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean blocked;
    private long quota;
}
