package ru.artosoft.vcsvpl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="t_user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UserEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false, unique = true)
        private String username;
        @Column(nullable = false, unique = true)
        private String email;
        @Column(nullable = false)
        private String password;
}
