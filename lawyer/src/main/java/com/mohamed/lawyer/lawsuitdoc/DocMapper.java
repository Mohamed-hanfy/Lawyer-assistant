package com.mohamed.lawyer.lawsuitdoc;

import org.springframework.stereotype.Service;

@Service
public class DocMapper {
    public Doc toDoc(DocRequest request){
        return Doc.builder()
                .name(request.fileName())
                .description(request.Description())
                .build();
    }

    public DocResponse toDocResponse(Doc doc){
        return DocResponse.builder()
                .name(doc.getName())
                .description(doc.getDescription())
                .createdDate(doc.getCreatedDate())
                .fileId(doc.getFileId())
                .lawsuitName(doc.getLawsuit() != null ? doc.getLawsuit().getName() : null)
                .build();
    }

    public SummaryResponse toSummaryResponse(String summary, String status){
        return SummaryResponse.builder()
                .summary(summary)
                .status(status)
                .build();
    }
}
