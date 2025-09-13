package com.mohamed.lawyer.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogsRepository extends JpaRepository<Logs, Long> {

    @Query("SELECT l FROM Logs l WHERE l.lawsuit.id = :lawsuitId")
    List<Logs> findByLawsuitId(Long lawsuitId);
    
}
