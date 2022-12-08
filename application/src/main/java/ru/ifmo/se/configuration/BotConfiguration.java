package ru.ifmo.se.configuration;

import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.se.properties.ApplicationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
@Slf4j
public class BotConfiguration {

    @Inject
    ApplicationProperties applicationProperties;

    public BotFrameworkHttpAdapter getAdapter() {
        return new BotFrameworkHttpAdapter(new BotProps(
                applicationProperties.bot().appId(),
                applicationProperties.bot().password()
        ));
    }

    public class BotProps implements Configuration {
        private Properties properties;

        public BotProps(String appid, String password) {
            properties = new Properties();
            log.info(appid);
            log.info(password);
            properties.put("MicrosoftAppId", appid);
            properties.put("MicrosoftAppPassword", password);
        }

        @Override
        public String getProperty(String s) {
            return null;
        }

        @Override
        public Properties getProperties() {
            return null;
        }

        @Override
        public String[] getProperties(String s) {
            return new String[0];
        }
    }
}
