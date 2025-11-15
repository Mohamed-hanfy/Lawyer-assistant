package com.mohamed.lawyer.lawsuit;

import com.mohamed.lawyer.lawyer.Lawyer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lawsuit")
public class Lawsuit  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String clientName;

    private String clientPhone;

    @CreationTimestamp
    private LocalDate date;

    @LastModifiedDate
    private LocalDate lastModified;

    @Enumerated
    private Status status;

    private String notes;

    boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "lawyer_id")
    private Lawyer lawyer;

    private String folderId;
}
