package com.mohamed.lawyer.whatsapp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppService {

    private final WebClient webClient;

    @Value("${whatsapp.api.key:150f07a1}")
    private String apiKey;

    @Value("k0AQIKc5X3JlyDYs")
    private String apiSecret;

    @Value("${whatsapp.from.number:14157386102}")
    private String fromNumber;

    @Value("${whatsapp.api.url:https://messages-sandbox.nexmo.com/v1/messages}")
    private String apiUrl;

    public Mono<WhatsAppResponse> sendMessage(String toPhoneNumber, String message) {
        WhatsAppRequest request = WhatsAppRequest.builder()
                .from(fromNumber)
                .to(toPhoneNumber)
                .messageType("text")
                .text(message)
                .channel("whatsapp")
                .build();

        String auth = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes(StandardCharsets.UTF_8));

        return webClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(WhatsAppResponse.class)
                .doOnSuccess(response -> log.info("WhatsApp message sent successfully to {}: {}", toPhoneNumber, response))
                .doOnError(error -> log.error("Failed to send WhatsApp message to {}: {}", toPhoneNumber, error.getMessage()));
    }

    public void sendLogNotification(String clientPhoneNumber, String clientName, String lawsuitName, String logMessage) {
        String message = String.format(
                "السلام عليكم %s 👋\n\n📋 تحديث جديد فالقضيه '%s':\n\n%s\n\n⏰ %s",
                clientName,
                lawsuitName,
                logMessage,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        sendMessage(clientPhoneNumber, message)
                .subscribe(
                        response -> log.info("Log notification sent to client {} ({})", clientName, clientPhoneNumber),
                        error -> log.error("Failed to send log notification to client {} ({}): {}", clientName, clientPhoneNumber, error.getMessage())
                );
    }
}
