package com.mohamed.lawyer.logs;

import lombok.Builder;

@Builder
public record LogsRequest(
        String message
) {
}
