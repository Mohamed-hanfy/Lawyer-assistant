package com.mohamed.lawyer.logs;

import com.mohamed.lawyer.lawsuit.Lawsuit;
import com.mohamed.lawyer.lawyer.Lawyer;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "logs")
@Tag(name = "Logs", description = "Logs API")
public class Logs implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "lawsuit_id")
    private Lawsuit lawsuit;

}
