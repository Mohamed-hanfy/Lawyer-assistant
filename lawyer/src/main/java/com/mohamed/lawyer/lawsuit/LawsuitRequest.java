package com.mohamed.lawyer.lawsuit;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LawsuitRequest(
        @NotNull
        String name,
        String clientName,
        String clientPhone,
        String description,
        String Notes,
        @NotNull
        @Enumerated
        Status status
) {
}
