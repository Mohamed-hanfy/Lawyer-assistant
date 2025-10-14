package com.mohamed.lawyer.lawsuitdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AnalysisResponse {
    private String law;
    private String analysis;
    private String status;
}
