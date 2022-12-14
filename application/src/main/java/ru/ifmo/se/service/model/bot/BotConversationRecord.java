package ru.ifmo.se.service.model.bot;

import lombok.Data;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Data
public class BotConversationRecord {
    private Instant createdTimestamp;
    private List<String> messages;
    private Boolean userRegistered;

    public BotConversationRecord() {
        createdTimestamp = Instant.now();
        messages = new LinkedList<>();
        userRegistered = false;
    }
}
