package ru.ifmo.se.service.model.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelData {
    private Message message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private MessageFrom from;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageFrom {
        private String id;
    }
}
