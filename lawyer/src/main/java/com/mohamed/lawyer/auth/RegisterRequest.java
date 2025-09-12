package com.mohamed.lawyer.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record RegisterRequest(
        @NotNull
        String firstName,
        @NonNull
        String lastName,
        @NotNull @Email
        String email,
        @NotNull
        String password
) {
}
