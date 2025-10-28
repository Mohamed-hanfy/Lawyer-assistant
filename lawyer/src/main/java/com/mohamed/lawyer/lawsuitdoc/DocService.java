package com.mohamed.lawyer.lawsuitdoc;

import com.google.api.services.drive.model.File;
import com.mohamed.lawyer.config.LegalAIConfig;
import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawsuit.LawsuitRepository;
import com.mohamed.lawyer.storage.GoogleDriveService;
import com.mohamed.lawyer.utils.LegalPromptSpecifications;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.mohamed.lawyer.config.LegalAIConfig.MAIN_SYSTEM_PROMPT;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository repository;
    private final DocMapper mapper;
    private final GoogleDriveService googleDriveService;
    private final LawsuitRepository lawsuitRepository;
    private final ChatModel chatModel;
    private final ChatClient chatClient;

    public Long save(byte[] file,
                     String fileName,
                     String description,
                     String mimeType,
                     Long lawsuitId) {

        Lawsuit lawsuit = lawsuitRepository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        String fileId = googleDriveService.uploadFile(file,
                fileName,
                mimeType,
                lawsuit.getFolderId());

        Doc doc = Doc.builder()
                .name(fileName)
                .description(description)
                .fileId(fileId)
                .lawsuit(lawsuit)
                .build();

        Doc savedDoc = repository.save(doc);

        return savedDoc.getId();
    }

    public List<DocResponse> getAllDocs(Long lawsuitId) {
        return repository.getAllByLawsuitId(lawsuitId)
                .stream()
                .map(mapper::toDocResponse)
                .toList();
    }

    public byte[] getDocById(String fileId) {
        return googleDriveService.downloadFile(fileId);
    }

    public File getFileMetadata(String fileId) {
        return googleDriveService.getFileMetadata(fileId);
    }

    public void deleteDocById(String fileId) {
        Doc doc = repository.findByFileId(fileId).orElseThrow(() -> new IllegalArgumentException("there is no file with this id"));
        repository.delete(doc);
        googleDriveService.deleteFile(fileId);
    }

    public Flux<String> analysisFile(String fileId, String specificLow) throws IOException {

        try (PDDocument document = Loader.loadPDF(getDocById(fileId))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String fileContent = stripper.getText(document);

            File file = googleDriveService.getFileMetadata(fileId);

            String userPrompt = String.format("""
                    اسم المستند: %s
                    مجال التركيز: %s
                    
                    محتوى المستند:
                    %s
                    
                    الرجاء تقديم تحليل قانوني متعمق مع التركيز على المجال القانوني المحدد.
                    """, file.getName(), specificLow, fileContent);


            return chatClient.prompt()
                    .system(LegalAIConfig.MAIN_SYSTEM_PROMPT + "\n\n" +
                            LegalPromptSpecifications.ANALYSIS_SPEC)
                    .user(userPrompt)
                    .options(OllamaOptions.create()
                            .withModel("tinydolphin")
                            .withTemperature(0.1f))
                    .stream()
                    .content();
        }

    }


    public Flux<String> summarizeInPoints(String fileId) throws IOException {
        try (PDDocument document = Loader.loadPDF(getDocById(fileId))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String fileContent = stripper.getText(document);

            File file = googleDriveService.getFileMetadata(fileId);

            String userPrompt = String.format("""
            اسم المستند: %s
            
            محتوى المستند:
            %s
            
            الرجاء تقديم ملخص شامل وفقاً للتنسيق المحدد.
            """, file.getName(), fileContent);

          return chatClient.prompt()
                    .system(LegalAIConfig.MAIN_SYSTEM_PROMPT + "\n\n" +
                            LegalPromptSpecifications.SUMMARIZATION_SPEC)
                    .user(userPrompt)
                    .options(OllamaOptions.create()
                            .withModel("tinydolphin")
                            .withTemperature(0.1f))
                    .stream()
                    .content();
        }

    }

}
