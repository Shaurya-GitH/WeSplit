package com.wesplit.main.repositories;

import com.wesplit.main.entities.Groups;
import com.wesplit.main.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupsRepository extends JpaRepository<Groups,Long> {
    List<Groups> findAllByMembers(User user);
}
