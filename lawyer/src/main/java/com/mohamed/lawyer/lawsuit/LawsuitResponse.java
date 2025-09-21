package com.mohamed.lawyer.lawsuit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LawsuitResponse {
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
}
