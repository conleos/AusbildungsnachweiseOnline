package com.conleos.data.repository;

import com.conleos.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("Select distinct u.id from User u")
    List<Integer> getAllUsers();

    @Query ("Select u from User u where u.username = :name")
    List<User> getUserByName(@Param("name") String name);

}
