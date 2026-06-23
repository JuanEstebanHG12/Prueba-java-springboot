package com.riwi.dbmanager.model;

import com.riwi.dbmanager.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();

    @LastModifiedDate
    @Builder.Default
    @Column(name = "updated_at", nullable = true)
    private LocalDate updatedAt = LocalDate.now();
}