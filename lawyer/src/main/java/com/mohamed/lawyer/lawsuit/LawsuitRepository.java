package com.mohamed.lawyer.lawsuit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LawsuitRepository extends JpaRepository<Lawsuit, Long> {

    @Override
    @Query("SELECT l FROM Lawsuit l WHERE l.id = :id and l.lawyer.id = ?#{principal.getId()}")
    Optional<Lawsuit> findById(Long id);

    @Query("SELECT l FROM Lawsuit l WHERE l.lawyer.id = ?#{principal.getId()} and l.isDeleted = false")
    List<Lawsuit> findLawsuitByLawyerId();

    @Query("SELECT l FROM Lawsuit l WHERE l.status = :status and l.lawyer.id = ?#{principal.getId()} and l.isDeleted = false")
    List<Lawsuit> findLawsuitByStatus(Status status);

    @Query("SELECT l FROM Lawsuit l WHERE l.clientName LIKE CONCAT('%', :clientName, '%') and l.lawyer.id = ?#{principal.getId()}")
    List<Lawsuit> findLawsuitByClientName(String clientName);

    @Query("SELECT l FROM Lawsuit l WHERE l.name LIKE CONCAT('%', :name, '%') and l.lawyer.id = ?#{principal.getId()} ")
    List<Lawsuit> findAllByLawsuitName(String name);

    @Query(
            value = "SELECT  *, MATCH(lawsuit.name,lawsuit.description) AGAINST(:searchTerm IN BOOLEAN MODE) as relevance" +
                    " FROM lawsuit" +
                    " WHERE MATCH(lawsuit.name, lawsuit.description) AGAINST(:searchTerm IN BOOLEAN MODE) AND  lawsuit.lawyer_id = :lawyerId " +
                    " ORDER BY relevance DESC",
            nativeQuery = true
    )
    List<Lawsuit> findByFullTextSearch(String searchTerm, Long lawyerId);


}
