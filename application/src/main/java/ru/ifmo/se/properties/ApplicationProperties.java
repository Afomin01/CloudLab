package ru.ifmo.se.properties;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface ApplicationProperties {

    SmbProperties smb();

    ScheduledProperties scheduled();

    UserProperties user();

    interface UserProperties {
        int defaultQuota();
    }

    interface ScheduledProperties {
        String datasetGenerationTaskStart();
    }

    interface SmbProperties{
        String serverUrl();
        String username();
        String password();

        String shareName();
    }
}
