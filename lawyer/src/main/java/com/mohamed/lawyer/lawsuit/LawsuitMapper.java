package com.mohamed.lawyer.lawsuit;

import org.springframework.stereotype.Service;

@Service
public class LawsuitMapper {
    public Lawsuit toLawsuit(LawsuitRequest request) {
        return Lawsuit.builder()
                .name(request.name())
                .description(request.description())
                .notes(request.Notes())
                .clientName(request.clientName())
                .clientPhone(request.clientPhone())
                .status(request.status())
                .build();
    }

    public LawsuitResponse toLawsuitResponse(Lawsuit lawsuit) {
        return LawsuitResponse.builder()
                .id(lawsuit.getId())
                .name(lawsuit.getName())
                .description(lawsuit.getDescription())
                .notes(lawsuit.getNotes())
                .clientName(lawsuit.getClientName())
                .clientPhone(lawsuit.getClientPhone())
                .date(lawsuit.getDate())
                .lastModified(lawsuit.getLastModified())
                .status(lawsuit.getStatus())
                .build();
    }
}
