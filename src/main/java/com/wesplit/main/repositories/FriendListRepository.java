package com.wesplit.main.repositories;

import com.wesplit.main.entities.FriendList;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList,Long> {

    Optional<FriendList> findByUser(User user);
}
