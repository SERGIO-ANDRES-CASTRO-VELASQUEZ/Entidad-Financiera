package com.prueba.banco.models;

import com.prueba.banco.models.ENUMS.TypeIdentity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="Clientes")
public class Customers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private TypeIdentity TypeIdentity;

    @Column(nullable = false, unique = true)
    private Integer NumberIdentity;

    @Column(nullable = false)
    private String FirstName;

    @Column(nullable = false)
    private String LastName;

    @Column(nullable = false, unique = true)
    private String Email;

    @Column(nullable = false)
    private Date BirthDate;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime CreatedDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime LastModifiedDate;

    @OneToMany( mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;




}
