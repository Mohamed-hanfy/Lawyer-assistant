package com.mohamed.lawyer.lawsuit;

import com.mohamed.lawyer.lawyer.Lawyer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LawsuitService {

    private final LawsuitRepository repository;
    private final LawsuitMapper lawsuitMapper;

    public List<LawsuitResponse> getAllLawsuits(){
       return repository.findLawsuitByLawyerId()
               .stream()
               .map(lawsuitMapper::toLawsuitResponse)
               .toList();
   }

   public Long addLawsuit(LawsuitRequest lawsuitRequest,
                             Authentication connectedUser){
        Lawyer lawyer = (Lawyer) connectedUser.getPrincipal();
        Lawsuit lawsuit = lawsuitMapper.toLawsuit(lawsuitRequest);
        lawsuit.setLawyer(lawyer);
        return repository.save(lawsuit).getId();
   }


    public List<LawsuitResponse> getLawsuitByStatus(Status status) {
        return repository.findLawsuitByStatus(status)
                .stream()
                .map(lawsuitMapper::toLawsuitResponse)
                .toList();
    }

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

}
