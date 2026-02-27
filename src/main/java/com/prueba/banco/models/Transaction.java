package com.prueba.banco.models;

import com.prueba.banco.models.ENUMS.TypeTransaction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Transacciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private TypeTransaction TypeTransaction;

    @Column(nullable = false)
    private Double amount;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime CreatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_cuenta_id", nullable = true)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_cuenta_id", nullable = true)
    private Account targetAccount;





}
