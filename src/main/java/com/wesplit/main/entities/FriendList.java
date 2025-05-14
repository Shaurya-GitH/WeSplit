package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class FriendList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendListId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "userId",name = "user_id")
    private User user;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = @JoinColumn(name="friendlist_id", referencedColumnName = "friendListId"),
            inverseJoinColumns = @JoinColumn(name="friend_id",referencedColumnName = "userId")
    )
    private List<User> friends;
}
