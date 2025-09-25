package com.mohamed.lawyer.lawsuitdoc;

import com.mohamed.lawyer.lawsuit.Lawsuit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "docs")
public class Doc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "lawsuit_id")
    private Lawsuit lawsuit;

    private String fileId;

    @CreationTimestamp
    private LocalDateTime createdDate;
}
