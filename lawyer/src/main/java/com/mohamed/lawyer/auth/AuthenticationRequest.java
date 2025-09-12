package com.mohamed.lawyer.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record AuthenticationRequest(@NotNull String email,
                                    @NonNull String password) {
}
