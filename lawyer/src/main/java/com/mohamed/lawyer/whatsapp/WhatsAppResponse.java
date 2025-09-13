package com.mohamed.lawyer.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WhatsAppResponse {
    @JsonProperty("message_uuid")
    private String messageUuid;
    private String status;
    private String timestamp;
}
