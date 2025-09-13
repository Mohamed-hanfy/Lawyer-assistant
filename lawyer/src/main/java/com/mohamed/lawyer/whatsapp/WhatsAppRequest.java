package com.mohamed.lawyer.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhatsAppRequest {
    private String from;
    private String to;
    @JsonProperty("message_type")
    private String messageType;
    private String text;
    private String channel;
}
