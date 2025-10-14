package com.mohamed.lawyer.lawsuitdoc;

import com.google.api.services.drive.model.File;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doc")
public class DocController {
    private final DocService service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String fileName,
            @RequestParam String description,
            @RequestParam Long lawsuitId
    ) throws IOException {
        return ResponseEntity.ok(service.save(file.getBytes(), fileName,description,file.getContentType(),lawsuitId));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String fileId
    ){

        File fileMetadata = service.getFileMetadata(fileId);

        byte[] fileContent = service.getDocById(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileMetadata.getMimeType()));
        headers.setContentDispositionFormData("attachment", fileMetadata.getName());
        headers.setContentLength(fileContent.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @GetMapping("/all-docs")
    public ResponseEntity<List<DocResponse>> getAllDocs(
            @RequestParam Long lawsuitId
    ){
        return ResponseEntity.ok(service.getAllDocs(lawsuitId));
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity deleteFile(
            @PathVariable String fileId
    ){
        service.deleteDocById(fileId);
        return ResponseEntity.noContent().build();
    }





}
