package com.mohamed.lawyer.lawsuit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LawsuitResponse  implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String notes;
    private String clientName;
    private String clientPhone;
    private LocalDate date;
    private LocalDate lastModified;
    private Status status;

    @JsonIgnore
    private double score;

    @JsonIgnore
    private String folderId;
}
