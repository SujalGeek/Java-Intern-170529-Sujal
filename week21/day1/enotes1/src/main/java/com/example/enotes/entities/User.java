package com.example.enotes.entities;

import java.util.List;
import jakarta.persistence.*; // Use * for easier imports
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String firstName;
    private String lastName;
    private String email;
    private String mobNo;
    private String password;
    
    // FIX 2 & 3: Changed OneToMany -> ManyToMany AND Removed CascadeType.ALL
    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
    
    
    @OneToOne(cascade = CascadeType.ALL)
    private AccountStatus status;
    
}