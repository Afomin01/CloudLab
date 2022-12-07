package ru.ifmo.se.configuration;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.Share;
import io.quarkus.runtime.Startup;
import lombok.extern.slf4j.Slf4j;
import ru.ifmo.se.configuration.model.DiskShareWrapper;
import ru.ifmo.se.properties.ApplicationProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
@Startup(1)
@Slf4j
public class SMBClientConfiguration {

    @Inject
    ApplicationProperties applicationProperties;

    @Startup(1)
    @Produces
    @ApplicationScoped
    public DiskShareWrapper produceSMBClient(){
        try {
            SMBClient smbClient = new SMBClient();
            Connection connection = smbClient.connect(applicationProperties.smb().serverUrl());
            AuthenticationContext authenticationContext = new AuthenticationContext(
                    applicationProperties.smb().username(),
                    applicationProperties.smb().password().toCharArray(),
                    ""
            );

            Session session = connection.authenticate(authenticationContext);

            DiskShare diskShare = (DiskShare) session.connectShare(applicationProperties.smb().shareName());
            log.info("Connected to smb share");
            return DiskShareWrapper
                    .builder()
                    .diskShare(diskShare)
                    .build();

        } catch (IOException e){
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
