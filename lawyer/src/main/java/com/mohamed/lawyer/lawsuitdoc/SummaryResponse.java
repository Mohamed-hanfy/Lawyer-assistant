package com.mohamed.lawyer.lawsuitdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SummaryResponse {
    private String summary;
    private String status;
}
