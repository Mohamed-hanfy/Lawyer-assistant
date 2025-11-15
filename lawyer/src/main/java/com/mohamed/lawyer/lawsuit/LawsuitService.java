package com.mohamed.lawyer.lawsuit;

import com.mohamed.lawyer.lawyer.Lawyer;
import com.mohamed.lawyer.storage.GoogleDriveService;
import com.mohamed.lawyer.utils.ArabicNormalizer;
import com.mohamed.lawyer.utils.FuzzyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class LawsuitService {

    private final LawsuitRepository repository;
    private final LawsuitMapper lawsuitMapper;
    private final GoogleDriveService googleDriveService;

    private final String CACHE_VALUE = "lawsuits";

    @Cacheable(value = CACHE_VALUE , key = "'all'")
    public List<LawsuitResponse> getAllLawsuits(){
       return repository.findLawsuitByLawyerId()
               .stream()
               .map(lawsuitMapper::toLawsuitResponse)
               .toList();
   }

   @CacheEvict(value = CACHE_VALUE , key = "'all'")
   public Long addLawsuit(LawsuitRequest lawsuitRequest,
                             Authentication connectedUser){
        Lawyer lawyer = (Lawyer) connectedUser.getPrincipal();
        Lawsuit lawsuit = lawsuitMapper.toLawsuit(lawsuitRequest);
        lawsuit.setLawyer(lawyer);
        lawsuit.setFolderId(googleDriveService.createFolder(lawsuit.getName(), null));
        return repository.save(lawsuit).getId();
   }

   @Cacheable(value = CACHE_VALUE , key = "#status")
    public List<LawsuitResponse> getLawsuitByStatus(Status status) {
        return repository.findLawsuitByStatus(status)
                .stream()
                .map(lawsuitMapper::toLawsuitResponse)
                .toList();
    }

    @Cacheable(value = CACHE_VALUE , key = "#clientName")
    public List<LawsuitResponse> getLawsuitByClientName(String clientName){
        return repository.findLawsuitByClientName(clientName)
                .stream()
                .map(lawsuitMapper::toLawsuitResponse)
                .toList();
    }

    public List<LawsuitResponse> getLawsuitByName(String name){
        return repository.findAllByLawsuitName(name)
                .stream()
                .map(lawsuitMapper::toLawsuitResponse)
                .toList();
    }

    public void markAsDeleted(Long lawsuitId){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));
        lawsuit.setDeleted(!lawsuit.isDeleted());
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public void updateStatus(Long lawsuitId,Status status){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        if(lawsuit.getStatus() == status){
            throw new IllegalArgumentException("Lawsuit status is already " + status);
        }
        lawsuit.setStatus(status);
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public void updateNotes(Long lawsuitId,String notes){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        lawsuit.setNotes(notes);
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public void updateDescription(Long lawsuitId,String description){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        lawsuit.setDescription(description);
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public void updateClientName(Long lawsuitId,String clientName){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        lawsuit.setClientName(clientName);
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public void updateClientPhone(Long lawsuitId,String clientPhone){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));

        lawsuit.setClientPhone(clientPhone);
        lawsuit.setLastModified(LocalDate.now());
        repository.save(lawsuit);
    }

    public List<LawsuitResponse> getFulltextFuzzy(String searchTerm, Authentication connectedUser) {
        Lawyer lawyer = (Lawyer) connectedUser.getPrincipal();
        String normalizedSearch = ArabicNormalizer.normalize(searchTerm);
        List<Lawsuit> fulltextResults = repository.findByFullTextSearch(searchTerm, lawyer.getId());
        List<Lawsuit> allLawsuits = repository.findLawsuitByLawyerId();
        Set<Long> fulltextIds = fulltextResults.stream()
                .map(Lawsuit::getId)
                .collect(Collectors.toSet());

        List<LawsuitResponse> results = fulltextResults.stream()
                .map(lawsuitMapper::toLawsuitResponse)
                .peek(dto -> dto.setScore(1.1))
                .toList();

        List<LawsuitResponse> fuzzyMatches = allLawsuits.stream()
                .filter(l -> !fulltextIds.contains(l.getId()))
                .map(lawsuitMapper::toLawsuitResponse)
                .peek(dto -> {
                    String normalizedName = ArabicNormalizer.normalize(dto.getName());
                    String normalizedDescription = ArabicNormalizer.normalize(dto.getDescription());

                    double score = Math.max(
                            FuzzyUtils.similarity(normalizedSearch, normalizedName),
                            FuzzyUtils.similarity(normalizedSearch, normalizedDescription)
                    );
                    dto.setScore(score);

                })
                .filter(dto -> dto.getScore() > 0.1)
                .toList();

        return Stream.concat(results.stream(), fuzzyMatches.stream())
                .sorted(Comparator.comparingDouble(LawsuitResponse::getScore).reversed())
                .toList();
    }

    @CacheEvict(value = CACHE_VALUE, allEntries = true)
    public Lawsuit updateLawsuit(Long lawsuitId,LawsuitRequest lawsuitRequest){
        Lawsuit lawsuit = repository.findById(lawsuitId)
                .orElseThrow(() -> new IllegalArgumentException("Lawsuit not found"));
        lawsuit.setName(lawsuitRequest.name());
        lawsuit.setDescription(lawsuitRequest.description());
        lawsuit.setClientName(lawsuitRequest.clientName());
        lawsuit.setClientPhone(lawsuitRequest.clientPhone());
        lawsuit.setLastModified(LocalDate.now());
        return repository.save(lawsuit);

    }
}
