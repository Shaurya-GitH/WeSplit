package com.wesplit.main.repositories;

import com.wesplit.main.entities.FriendList;
import com.wesplit.main.entities.User;
import com.wesplit.main.payloads.FriendDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList,Long> {

    @Query("select new com.wesplit.main.payloads.FriendDTO(u.firstName,u.lastName,u.email) from FriendList f JOIN f.friends u where f.user=:user")
    List<FriendDTO> getByUser(@Param("user") User user);
}
