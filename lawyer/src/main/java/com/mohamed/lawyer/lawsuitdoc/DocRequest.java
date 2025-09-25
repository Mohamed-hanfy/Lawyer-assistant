package com.mohamed.lawyer.lawsuitdoc;

import com.google.api.services.drive.model.File;
import jakarta.validation.constraints.NotEmpty;

public record DocRequest(
        @NotEmpty
        String fileName,
        String Description,
        String filePath
) {
}
