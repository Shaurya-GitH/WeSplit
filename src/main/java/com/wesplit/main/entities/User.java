package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "emailIndex", columnList = "email")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "friendList")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String firstName;
    private String lastName;
    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    private boolean registerStatus;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @OneToOne(mappedBy = "user",orphanRemoval = true,cascade = CascadeType.ALL)
    private FriendList friendList;
}
