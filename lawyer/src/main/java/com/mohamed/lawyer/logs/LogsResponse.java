package com.mohamed.lawyer.logs;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogsResponse implements Serializable {
    private Long id;
    private String message;
    private LocalDateTime date;
    private String lawsuitName;

}
