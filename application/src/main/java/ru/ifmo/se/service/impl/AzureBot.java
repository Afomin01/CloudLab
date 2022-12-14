package ru.ifmo.se.service.impl;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.ifmo.se.configuration.BotConfiguration;
import ru.ifmo.se.database.model.GenerationTaskEntity;
import ru.ifmo.se.database.model.UserEntity;
import ru.ifmo.se.database.repository.GenerationTaskRepository;
import ru.ifmo.se.database.repository.UserRepository;
import ru.ifmo.se.service.model.bot.BotConversationRecord;
import ru.ifmo.se.service.model.bot.ChannelData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
public class AzureBot extends ActivityHandler {

    @Inject
    BotConfiguration botConfiguration;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    UserRepository userRepository;

    @Inject
    GenerationTaskRepository generationTaskRepository;

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        String message = turnContext.getActivity().getText();

        String userId = getUserIdFromChannel(turnContext);
        botConfiguration.addMessageToConversation(userId, message);

        if (StringUtils.isNotEmpty(message)) {
            if (message.startsWith("/")) {
                return processCommand(turnContext, userId);
            }
        }

        if (!isRegistered(userId)) {
            List<String> messages = botConfiguration.getConversation(userId).getMessages();

            if (messages.size() > 2) {
                String prevMessage = messages.get(messages.size() - 2);
                if (Objects.equals(prevMessage, "/register")) {
                    return register(turnContext, userId);
                }
            }

            return turnContext.sendActivity(
                    MessageFactory.text("Register by using /register")
            ).thenApply(sendResult -> null);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        for (String msg : botConfiguration.getConversation(userId).getMessages()) {
            stringBuilder.append(msg).append("\n");
        }

        return turnContext.sendActivity(
                MessageFactory.text("Conversation: " + stringBuilder)
        ).thenApply(sendResult -> null);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
            List<ChannelAccount> membersAdded,
            TurnContext turnContext
    ) {
        String welcomeText = "Hello and welcome!";
        return membersAdded.stream()
                .filter(
                        member -> !StringUtils
                                .equals(member.getId(), turnContext.getActivity().getRecipient().getId())
                ).map(channel -> turnContext.sendActivity(MessageFactory.text(welcomeText, welcomeText, null)))
                .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }

    @Transactional
    public CompletableFuture<Void> register(TurnContext turnContext, String userId) {
        String message = turnContext.getActivity().getText();

        UserEntity user = userRepository.findByBotToken(message);
        if (user == null) {
            return turnContext.sendActivity(
                    MessageFactory.text("Invalid token")
            ).thenApply(sendResult -> null);
        }

        user.setTelegramId(userId);

        botConfiguration.getConversation(userId).setUserRegistered(true);

        return turnContext.sendActivity(
                MessageFactory.text("Registered!")
        ).thenApply(sendResult -> null);
    }

    @Transactional
    public boolean isRegistered(String userId) {
        BotConversationRecord conversationRecord = botConfiguration.getConversation(userId);

        if(conversationRecord.getUserRegistered() == null){
            return false;
        }

        if (conversationRecord.getUserRegistered()) {
            return true;
        }

        UserEntity user = userRepository.findByTelegramId(userId);
        if (user != null) {
            conversationRecord.setUserRegistered(true);
            return true;
        }

        return false;
    }


    public CompletableFuture<Void> processCommand(TurnContext turnContext, String userId) {
        String message = turnContext.getActivity().getText();

        switch (message) {
            case "/start" -> {
                return turnContext.sendActivity(
                        MessageFactory.text(" Commands: \n\n/register - to register using token \n\n/tasks - to check tasks")
                ).thenApply(sendResult -> null);
            }
            case "/register" -> {
                if(isRegistered(userId)){
                    return turnContext.sendActivity(
                            MessageFactory.text("Already registered.")
                    ).thenApply(sendResult -> null);
                }
                return turnContext.sendActivity(
                        MessageFactory.text("Enter token.")
                ).thenApply(sendResult -> null);
            }
            case "/tasks" -> {
                if(!isRegistered(userId)){
                    return turnContext.sendActivity(
                            MessageFactory.text("Register to see your tasks.")
                    ).thenApply(sendResult -> null);
                }
                return getTasksString(turnContext, userId);
            }
        }
        return null;
    }

    @Transactional
    public CompletableFuture<Void> getTasksString(TurnContext turnContext, String userId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Here are your tasks:").append("\n\n");

        UserEntity user = userRepository.findByTelegramId(userId);

        List<GenerationTaskEntity> generationTaskEntities = generationTaskRepository.findByUser(user);

        for (GenerationTaskEntity entity : generationTaskEntities) {
            stringBuilder.append("Id: ").append(entity.getId()).append("\n\n");
            stringBuilder.append("Creation time:").append(entity.getCreationTime().toString()).append("\n\n");
            stringBuilder.append("Status: ").append(entity.getStatus().toString()).append("\n\n");
            stringBuilder.append("*************************************").append("\n\n");
        }

        return turnContext.sendActivity(
                MessageFactory.text(stringBuilder.toString())
        ).thenApply(sendResult -> null);
    }

    private String getUserIdFromChannel(TurnContext turnContext) {
        try {
            String channelDataJson = objectMapper.writeValueAsString(turnContext.getActivity().getChannelData());
            ChannelData channelData = objectMapper.readValue(channelDataJson, ChannelData.class);
            return channelData.getMessage().getFrom().getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
