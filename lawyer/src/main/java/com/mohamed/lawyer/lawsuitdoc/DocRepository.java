package com.mohamed.lawyer.lawsuitdoc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocRepository extends JpaRepository<Doc, Long> {
    @Query("select d from Doc as d where d.lawsuit.id = :lawsuitId and d.lawsuit.lawyer.id = ?#{principal.getId()}")
    List<Doc> getAllByLawsuitId(Long lawsuitId);

    @Query("select d from Doc as d where d.fileId = :fileId and d.lawsuit.lawyer.id =?#{principal.getId()}")
    Optional<Doc> findByFileId(String fileId);


}
