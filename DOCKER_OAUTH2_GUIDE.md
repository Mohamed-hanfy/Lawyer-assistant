# Docker Setup Guide with Google Drive OAuth2

This guide explains how to run your Lawyer Application in Docker with Google Drive OAuth2 authentication.

## Prerequisites

1. Docker and Docker Compose installed
2. Google Cloud Project with Drive API enabled
3. OAuth2 credentials (client_secret.json) downloaded from Google Cloud Console

## Directory Structure

```
lawyer/
├── docker-compose.yml
├── .env
├── lawyer/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── mohamed/
│                       └── lawyer/
│                           └── storage/
│                               ├── client_secret.json  (YOUR OAUTH2 CREDENTIALS)
│                               └── tokens/              (OAUTH2 TOKENS - WILL BE CREATED)
```

## Setup Instructions

### Step 1: Configure Environment Variables

Copy the example environment file and configure it:

```bash
cp .env.example .env
```

Edit `.env` with your preferred settings:
- Database credentials
- Application port
- Other configurations

### Step 2: Place OAuth2 Credentials

Ensure your `client_secret.json` file is located at:
```
lawyer/src/main/java/com/mohamed/lawyer/storage/client_secret.json
```

**IMPORTANT:** This file contains sensitive information. Never commit it to version control.

### Step 3: Create Tokens Directory

```bash
mkdir -p lawyer/src/main/java/com/mohamed/lawyer/storage/tokens
```

## Running the Application

### Option A: First-Time Setup (OAuth2 Authorization Required)

When running for the first time, you need to authorize the application with Google:

1. **Start the services in interactive mode:**

```bash
docker-compose up
```

2. **Monitor the logs** for OAuth2 authorization URL:

```
lawyer-app | Please open the following address in your browser:
lawyer-app | https://accounts.google.com/o/oauth2/auth?client_id=...
```

3. **Complete Authorization:**
   - Open the URL in your browser
   - Sign in with your Google account
   - Grant the requested permissions
   - You'll be redirected to `http://localhost:8080` (or another port 8081-8090)

4. **Verify Success:**
   - Check logs for: "Google Drive service initialized and tested successfully"
   - OAuth2 tokens will be saved in the `tokens/` directory

### Option B: Subsequent Runs (Tokens Already Exist)

Once you have authorized and tokens exist in the `tokens/` directory:

```bash
# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f app
```

### Option C: Using Pre-Generated Tokens

If you have already authorized the application locally:

1. Copy the tokens from your local development:
```bash
cp -r lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/* \
     lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/
```

2. Start the application:
```bash
docker-compose up -d
```

## OAuth2 Token Management

### Token Persistence

Tokens are stored in a Docker volume mapped to:
- **Host:** `lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/`
- **Container:** `/app/tokens/`

This ensures tokens persist across container restarts.

### Token Refresh

Google OAuth2 tokens include:
- **Access Token:** Short-lived (1 hour)
- **Refresh Token:** Long-lived (used to get new access tokens)

The application automatically refreshes tokens when needed.

### Token Revocation

If you need to revoke access:

1. Delete the tokens directory:
```bash
rm -rf lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/*
```

2. Restart the application (it will require re-authorization)

3. Or revoke via Google Account settings:
   - Go to: https://myaccount.google.com/permissions
   - Find "Lawyer Application"
   - Click "Remove Access"

## Troubleshooting

### Issue: OAuth2 Authorization Fails

**Symptom:** Application can't start, logs show OAuth errors

**Solutions:**
1. Verify `client_secret.json` is correctly placed
2. Ensure ports 8080-8090 are accessible
3. Check that OAuth2 redirect URIs in Google Cloud Console include:
   - `http://localhost:8080`
   - `http://localhost:8081`
   - ... (up to 8090)

### Issue: Tokens Directory Not Writable

**Symptom:** "Permission denied" errors when writing tokens

**Solutions:**
```bash
# Fix permissions
chmod 755 lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/

# Or run with proper user mapping
docker-compose down
docker-compose up --force-recreate
```

### Issue: Application Can't Connect to MySQL

**Symptom:** Connection refused or timeout errors

**Solutions:**
1. Wait for MySQL to be fully ready (health check)
2. Check MySQL logs: `docker-compose logs mysql`
3. Verify database credentials in `.env`

### Issue: Port Already in Use

**Symptom:** "Port 8080 is already allocated"

**Solutions:**
```bash
# Change the port in .env
echo "APP_PORT=8081" >> .env

# Or stop conflicting service
docker ps
docker stop <conflicting-container>
```

## Docker Commands Reference

```bash
# Build and start services
docker-compose up --build

# Start in detached mode
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# View app logs only
docker-compose logs -f app

# Restart app service
docker-compose restart app

# Rebuild app without cache
docker-compose build --no-cache app

# Remove all containers and volumes
docker-compose down -v
```

## Production Considerations

### Security Best Practices

1. **Credentials Management:**
   ```bash
   # Never commit these files
   echo "lawyer/src/main/java/com/mohamed/lawyer/storage/client_secret.json" >> .gitignore
   echo "lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/" >> .gitignore
   ```

2. **Use Docker Secrets (Docker Swarm):**
   ```yaml
   secrets:
     google_credentials:
       file: ./client_secret.json
   ```

3. **Environment-Specific Configs:**
   - Use separate `.env` files for dev/staging/prod
   - Never expose sensitive ports publicly

### Monitoring

1. **Health Check:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. **Google Drive Service Status:**
   ```bash
   curl http://localhost:8080/api/drive/status
   ```

### Backup

1. **Backup Tokens:**
   ```bash
   tar -czf tokens-backup-$(date +%Y%m%d).tar.gz \
     lawyer/src/main/java/com/mohamed/lawyer/storage/tokens/
   ```

2. **Backup Database:**
   ```bash
   docker-compose exec mysql mysqldump -u root -p lawyer_db > backup.sql
   ```

## Alternative: Headless OAuth2 (Advanced)

For server environments without browser access, consider:

1. **Service Accounts** (recommended for production)
2. **Pre-authorized tokens** (copy from development)
3. **Remote authorization** (authorize on local machine, copy tokens)

### Using Service Accounts

Service accounts don't require interactive OAuth2 flow:

1. Create a service account in Google Cloud Console
2. Download the JSON key file
3. Share Google Drive folders with the service account email
4. Modify `GoogleDriveService.java` to use service account authentication

Example service account authentication:
```java
GoogleCredential credential = GoogleCredential
    .fromStream(new FileInputStream("service-account-key.json"))
    .createScoped(Collections.singleton(DriveScopes.DRIVE));
```

## Support

For issues specific to:
- **Docker:** Check logs with `docker-compose logs`
- **OAuth2:** Verify Google Cloud Console settings
- **Application:** Check Spring Boot logs in container

## License

[Your License Here]
