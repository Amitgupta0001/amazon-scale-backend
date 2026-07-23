package com.amazonscale.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;

    @Column(nullable = false,unique = true,length = 100)
    private String email;

    @Column(nullable = false,length = 255)
    private String password;

    @Enumerated(EnumType.STRING)  // much safer and easier to read
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true; //Instead of deleting the account the user can no  longer login but it's historic data will be there

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Automatically set timestamps before inserting a new record
    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Automatically update timestamp before updating an existing record
    @PreUpdate
    public void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

}
