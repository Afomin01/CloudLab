package ru.ifmo.se.configuration;

import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.se.properties.ApplicationProperties;
import ru.ifmo.se.service.model.bot.BotConversationRecord;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
@Slf4j
public class BotConfiguration {

    @Inject
    ApplicationProperties applicationProperties;

    private BotFrameworkHttpAdapter botFrameworkHttpAdapter;

    private Map<String, BotConversationRecord> conversations = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        botFrameworkHttpAdapter = new BotFrameworkHttpAdapter(new BotProps(
                applicationProperties.bot().appId(),
                applicationProperties.bot().password()
        ));
    }

    public BotConversationRecord getConversation(String userId) {
        return conversations.get(userId);
    }

    public void addMessageToConversation(String userId, String message) {
        BotConversationRecord record = conversations.get(userId);

        if (record == null) {
            record = new BotConversationRecord();
            record.getMessages().add(message);
            conversations.put(userId, record);
        } else {
            record.getMessages().add(message);
        }
    }

    @Scheduled(every = "15m")
    public void clearOldConversations() {
        conversations.entrySet()
                .stream()
                .filter(entry -> Instant.now().minus(15, ChronoUnit.MINUTES).compareTo(entry.getValue().getCreatedTimestamp()) < 0)
                .forEach(entry -> conversations.remove(entry.getKey()));
    }

    public BotFrameworkHttpAdapter getAdapter() {
        return botFrameworkHttpAdapter;
    }

    public class BotProps implements Configuration {
        private Properties properties;

        public BotProps(String appid, String password) {
            properties = new Properties();
            properties.put("MicrosoftAppId", appid);
            properties.put("MicrosoftAppPassword", password);
        }

        public String getProperty(String key) {
            return this.properties.getProperty(key);
        }

        public Properties getProperties() {
            return this.properties;
        }

        public String[] getProperties(String key) {
            String baseProperty = this.properties.getProperty(key);
            if (baseProperty != null) {
                String[] splitProperties = baseProperty.split(",");
                return splitProperties;
            } else {
                return null;
            }
        }
    }
}
