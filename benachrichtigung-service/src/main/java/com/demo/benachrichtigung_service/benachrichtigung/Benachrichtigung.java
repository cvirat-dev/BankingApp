package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "benachrichtigung_typ", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter @NoArgsConstructor
public class Benachrichtigung {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private BenachrichtigungTyp typ;
    private String nachricht;
    private LocalDateTime timestamp;
}
