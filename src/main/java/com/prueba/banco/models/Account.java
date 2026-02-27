package com.prueba.banco.models;


import com.prueba.banco.models.ENUMS.TypeAccount;
import com.prueba.banco.models.ENUMS.TypeState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Cuentas")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private TypeAccount TypeAccount;

    @Column(nullable = false, unique = true)
    private Integer NumberAccount;

    @Column(nullable = false)
    private TypeState TypeState;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Boolean GMFexempt = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime CreatedDate;


    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime LastModifiedDate;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customers customer;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> outGoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> inGoingTransactions = new ArrayList<>();






}
