package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendListId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "userId",name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name="friendlist_id", referencedColumnName = "friendListId"),
            inverseJoinColumns = @JoinColumn(name="friend_id",referencedColumnName = "userId")
    )
    private List<User> friends;
}
