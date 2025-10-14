package com.mohamed.lawyer.lawsuitdoc;

import com.google.api.services.drive.model.File;
import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawsuit.LawsuitRepository;
import com.mohamed.lawyer.storage.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository repository;
    private final DocMapper mapper;
    private final GoogleDriveService googleDriveService;
    private final LawsuitRepository lawsuitRepository;
    private final ChatModel chatModel;

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

    public String analysisFile(String fileId, String specificLow) throws IOException {

        try (PDDocument document = Loader.loadPDF(getDocById(fileId))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String fileContent = stripper.getText(document);

            String prompt = analysisPrompt(fileContent, specificLow);

            OllamaOptions options = OllamaOptions.create()
                    .withModel("deepseek-r1");

            Prompt ollamaPrompt = new Prompt(prompt, options);
            ChatResponse response = chatModel.call(ollamaPrompt);
            return response.getResult().getOutput().getContent();
        }

    }


    public String summarizeInPoints(String fileId) throws IOException{
        try (PDDocument document = Loader.loadPDF(getDocById(fileId))) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String fileContent = stripper.getText(document);

            String prompt = "قم بتلخيص المستند التالي في نقاط واضحة ومختصرة:\n\n" +
                    "Summarize the following document in clear and concise points:\n\n" +
                    "---\n" + fileContent + "\n---\n\n" +
                    "استخدم الترقيم والتنسيق الواضح.\nUse numbering and clear formatting.";

            return chatModel.call(prompt);
        }
    }

    private String analysisPrompt(String documentText, String specificLaw) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("أنت محلل قانوني متخصص في القانون المصري.\n\n");
        prompt.append("You are a legal analyst specialized in Egyptian law.\n\n");

        if (specificLaw != null && !specificLaw.trim().isEmpty()) {
            prompt.append("قم بتحليل المستند التالي بناءً على: ").append(specificLaw).append("\n");
            prompt.append("Analyze the following document based on: ").append(specificLaw).append("\n\n");
        } else {
            prompt.append("قم بتحليل المستند التالي بناءً على القوانين المصرية المعمول بها.\n");
            prompt.append("Analyze the following document based on applicable Egyptian laws.\n\n");
        }

        prompt.append("المستند:\n");
        prompt.append("Document:\n");
        prompt.append("---\n");
        prompt.append(documentText);
        prompt.append("\n---\n\n");

        prompt.append("يرجى تقديم التحليل في نقاط واضحة تتضمن:\n");
        prompt.append("Please provide the analysis in clear points including:\n");
        prompt.append("1. ملخص المستند (Document Summary)\n");
        prompt.append("2. المواد القانونية المعنية (Relevant Legal Articles)\n");
        prompt.append("3. التحليل القانوني (Legal Analysis)\n");
        prompt.append("4. الملاحظات والتوصيات (Observations and Recommendations)\n");
        prompt.append("5. المخاطر القانونية المحتملة (Potential Legal Risks)\n\n");

        prompt.append("استخدم اللغة العربية والإنجليزية في الإجابة.\n");
        prompt.append("Use both Arabic and English in your response.\n");

        return prompt.toString();
    }


}
