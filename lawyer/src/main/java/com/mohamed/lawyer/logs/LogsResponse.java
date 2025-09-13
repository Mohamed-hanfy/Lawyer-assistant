package com.mohamed.lawyer.logs;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogsResponse {
    private Long id;
    private String message;
    private LocalDateTime date;
}
