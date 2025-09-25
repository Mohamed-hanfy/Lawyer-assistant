package com.mohamed.lawyer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.drive")
@Data
public class GoogleDriveConfig {

    private String applicationName = "Lawyer Application";
    private String credentialsPath = "lawyer/src/main/java/com/mohamed/lawyer/storage/cred.json";
    private String defaultFolderId;
    private int maxResults = 50;

    // Folder IDs for different document types
    private String contractsFolderId;
    private String evidenceFolderId;
    private String clientDocumentsFolderId;
    private String caseFilesFolderId;
}
