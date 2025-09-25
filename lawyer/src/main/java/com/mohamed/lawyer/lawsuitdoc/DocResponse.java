package com.mohamed.lawyer.lawsuitdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DocResponse {
    private String name;
    private String description;
    private String fileId;
    private String lawsuitName;
    private LocalDateTime createdDate;
}
