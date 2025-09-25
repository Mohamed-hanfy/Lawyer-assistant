package com.mohamed.lawyer.lawsuitdoc;

import com.google.api.services.drive.model.File;
import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawsuit.LawsuitRepository;
import com.mohamed.lawyer.storage.GoogleDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocService {
    private final DocRepository repository;
    private final DocMapper mapper;
    private final GoogleDriveService googleDriveService;
    private final LawsuitRepository lawsuitRepository;

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

}
