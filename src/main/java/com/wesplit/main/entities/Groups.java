package com.wesplit.main.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;
    @Column
    private String groupName;
    @Column
    private LocalDate createdAt;

    @ManyToMany
    @JoinTable(
          joinColumns = @JoinColumn(name ="groups_id" ,referencedColumnName = "groupId"),
            inverseJoinColumns = @JoinColumn(name ="member_id" ,referencedColumnName ="userId" )
    )
    private List<User> members;

}
