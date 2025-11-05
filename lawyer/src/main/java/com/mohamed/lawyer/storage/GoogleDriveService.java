package com.mohamed.lawyer.storage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class GoogleDriveService {

    private static final String APPLICATION_NAME = "Lawyer Application";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Use environment variables for Docker compatibility, fallback to local paths for development
    private static final String CREDENTIALS_FILE_PATH = System.getenv().getOrDefault(
            "GOOGLE_CREDENTIALS_PATH",
            "lawyer/src/main/java/com/mohamed/lawyer/storage/client_secret.json"
    );
    private static final String TOKENS_DIRECTORY_PATH = System.getenv().getOrDefault(
            "GOOGLE_TOKENS_PATH",
            "lawyer/src/main/java/com/mohamed/lawyer/storage/tokens"
    );

    private Drive driveService;

    @PostConstruct
    public void initializeDriveService() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Load client secrets
            InputStream credentialsStream = new FileInputStream(CREDENTIALS_FILE_PATH);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialsStream));

            // Validate client secrets
            if (clientSecrets.getDetails() == null ||
                    clientSecrets.getDetails().getClientId() == null ||
                    clientSecrets.getDetails().getClientSecret() == null) {
                throw new IllegalStateException("Invalid client secrets file. Make sure it's for a Desktop application.");
            }

            log.info("Loaded client secrets for: {}", clientSecrets.getDetails().getClientId());

            // Build flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(DriveScopes.DRIVE))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .setApprovalPrompt("force") // Force consent screen
                    .build();

            // Try different port if 8080 is busy
            LocalServerReceiver receiver = null;
            for (int port = 8080; port <= 8090; port++) {
                try {
                    receiver = new LocalServerReceiver.Builder()
                            .setPort(port)
                            .build();
                    log.info("Using port {} for OAuth callback", port);
                    break;
                } catch (Exception e) {
                    log.warn("Port {} is busy, trying next...", port);
                }
            }

            if (receiver == null) {
                throw new RuntimeException("Could not find available port for OAuth callback");
            }

            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Build Drive service
            driveService = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Test the connection
            driveService.about().get().setFields("user").execute();
            log.info("Google Drive service initialized and tested successfully");

        } catch (IOException | GeneralSecurityException e) {
            log.error("Failed to initialize Google Drive service", e);
            throw new RuntimeException("Failed to initialize Google Drive service: " + e.getMessage(), e);
        }
    }

    /**
     * Upload a file to Google Drive
     * 
     * @param filePath Local file path
     * @param fileName Name for the file in Google Drive
     * @param mimeType MIME type of the file
     * @param parentFolderId Optional parent folder ID (null for root)
     * @return File ID of the uploaded file
     */
    public String uploadFile(String filePath, String fileName, String mimeType, String parentFolderId) {
        try {
            // File metadata
            File fileMetadata = new File();
            fileMetadata.setName(fileName);

            if (parentFolderId != null) {
                fileMetadata.setParents(Collections.singletonList(parentFolderId));
            }

            // File content
            java.io.File localFile = new java.io.File(filePath);
            FileContent mediaContent = new FileContent(mimeType, localFile);

            // Upload file
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setSupportsAllDrives(true)
                    .setFields("id, name, size, mimeType, createdTime")
                    .execute();

            log.info("File uploaded successfully: {} with ID: {}", fileName, uploadedFile.getId());
            return uploadedFile.getId();

        } catch (IOException e) {
            log.error("Error uploading file: {}", fileName, e);
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        }
    }

    /**
     * Upload file from byte array
     * 
     * @param fileData File content as byte array
     * @param fileName Name for the file in Google Drive
     * @param mimeType MIME type of the file
     * @param parentFolderId Optional parent folder ID (null for root)
     * @return File ID of the uploaded file
     */
    public String uploadFile(byte[] fileData, String fileName, String mimeType, String parentFolderId) {
        try {
            // Create a temporary file
            java.io.File tempFile = java.io.File.createTempFile("upload", ".tmp");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                fos.write(fileData);
            }

            String fileId = uploadFile(tempFile.getAbsolutePath(), fileName, mimeType, parentFolderId);

            // Clean up temporary file
            tempFile.delete();

            return fileId;

        } catch (IOException e) {
            log.error("Error uploading file from byte array: {}", fileName, e);
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        }
    }

    /**
     * Download file content from Google Drive
     * 
     * @param fileId Google Drive file ID
     * @return File content as byte array
     */
    public byte[] downloadFile(String fileId) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);

            byte[] content = outputStream.toByteArray();
            log.info("File downloaded successfully: {} bytes", content.length);
            return content;

        } catch (IOException e) {
            log.error("Error downloading file with ID: {}", fileId, e);
            throw new RuntimeException("Failed to download file with ID: " + fileId, e);
        }
    }

    /**
     * Get file metadata
     * 
     * @param fileId Google Drive file ID
     * @return File metadata
     */
    public File getFileMetadata(String fileId) {
        try {
            File file = driveService.files().get(fileId)
                    .setFields("id, name, size, mimeType, createdTime, modifiedTime, parents")
                    .execute();

            log.info("Retrieved metadata for file: {}", file.getName());
            return file;

        } catch (IOException e) {
            log.error("Error getting metadata for file with ID: {}", fileId, e);
            throw new RuntimeException("Failed to get file metadata for ID: " + fileId, e);
        }
    }

    /**
     * List files in Google Drive
     * 
     * @param folderId Parent folder ID (null for root)
     * @param maxResults Maximum number of results to return
     * @return List of files
     */
    public List<File> listFiles(String folderId, int maxResults) {
        try {
            String query = folderId != null ? "parents = '" + folderId + "'" : null;

            FileList result = driveService.files().list()
                    .setQ(query)
                    .setPageSize(maxResults)
                    .setFields("nextPageToken, files(id, name, size, mimeType, createdTime, modifiedTime)")
                    .execute();

            List<File> files = result.getFiles();
            log.info("Retrieved {} files", files.size());
            return files;

        } catch (IOException e) {
            log.error("Error listing files in folder: {}", folderId, e);
            throw new RuntimeException("Failed to list files", e);
        }
    }

    /**
     * Search files by name
     * 
     * @param fileName File name to search for
     * @param maxResults Maximum number of results
     * @return List of matching files
     */
    public List<File> searchFilesByName(String fileName, int maxResults) {
        try {
            String query = "name contains '" + fileName + "'";

            FileList result = driveService.files().list()
                    .setQ(query)
                    .setPageSize(maxResults)
                    .setFields("nextPageToken, files(id, name, size, mimeType, createdTime, modifiedTime)")
                    .execute();

            List<File> files = result.getFiles();
            log.info("Found {} files matching name: {}", files.size(), fileName);
            return files;

        } catch (IOException e) {
            log.error("Error searching files by name: {}", fileName, e);
            throw new RuntimeException("Failed to search files by name: " + fileName, e);
        }
    }

    /**
     * Delete file from Google Drive
     * 
     * @param fileId File ID to delete
     */
    public void deleteFile(String fileId) {
        try {
            driveService.files().delete(fileId).execute();
            log.info("File deleted successfully: {}", fileId);

        } catch (IOException e) {
            log.error("Error deleting file with ID: {}", fileId, e);
            throw new RuntimeException("Failed to delete file with ID: " + fileId, e);
        }
    }

    /**
     * Create a folder in Google Drive
     * 
     * @param folderName Name of the folder
     * @param parentFolderId Parent folder ID (null for root)
     * @return Folder ID
     */
    public String createFolder(String folderName, String parentFolderId) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            if (parentFolderId != null) {
                fileMetadata.setParents(Collections.singletonList(parentFolderId));
            }

            File folder = driveService.files().create(fileMetadata)
                    .setFields("id, name")
                    .execute();

            log.info("Folder created successfully: {} with ID: {}", folderName, folder.getId());
            return folder.getId();

        } catch (IOException e) {
            log.error("Error creating folder: {}", folderName, e);
            throw new RuntimeException("Failed to create folder: " + folderName, e);
        }
    }

    /**
     * Move file to another folder
     * 
     * @param fileId File ID to move
     * @param newParentId New parent folder ID
     */
    public void moveFile(String fileId, String newParentId) {
        try {
            // Get current parents
            File file = driveService.files().get(fileId).setFields("parents").execute();
            String previousParents = String.join(",", file.getParents());

            // Move file
            driveService.files().update(fileId, null)
                    .setAddParents(newParentId)
                    .setRemoveParents(previousParents)
                    .execute();

            log.info("File moved successfully: {} to folder: {}", fileId, newParentId);

        } catch (IOException e) {
            log.error("Error moving file: {} to folder: {}", fileId, newParentId, e);
            throw new RuntimeException("Failed to move file: " + fileId, e);
        }
    }

    /**
     * Share file with specific email address
     * 
     * @param fileId File ID to share
     * @param emailAddress Email address to share with
     * @param role Permission role (reader, writer, owner)
     */
    public void shareFile(String fileId, String emailAddress, String role) {
        try {
            com.google.api.services.drive.model.Permission permission = 
                new com.google.api.services.drive.model.Permission()
                    .setType("user")
                    .setRole(role)
                    .setEmailAddress(emailAddress);

            driveService.permissions().create(fileId, permission).execute();
            log.info("File shared successfully: {} with {} as {}", fileId, emailAddress, role);

        } catch (IOException e) {
            log.error("Error sharing file: {} with {}", fileId, emailAddress, e);
            throw new RuntimeException("Failed to share file: " + fileId, e);
        }
    }

    /**
     * Check if service is initialized and ready
     * 
     * @return true if service is ready
     */
    public boolean isServiceReady() {
        return driveService != null;
    }
}
