package com.conleos.data.repository;

import com.conleos.data.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("Select u from User u")
    List<User> getAllUsers();

    @Query("Select distinct u.id from User u")
    List<Integer> getAllUserChannels();

    @Query("Select u from User u where u.id = :_id")
    List<User> getUserByChannel(@Param("_id") Long _id);

    @Query("Select u from User u where u.username = :name")
    List<User> getUserByName(@Param("name") String name);

    @Query("Select u.id from User u where :_assignee MEMBER OF u.assigneeIDs")
    List<Long> getUsersByAssignee(@Param("_assignee") Long _assignee);

    @Transactional
    @Modifying
    @Query("Update User u Set u.passwordHash = :_password where u.id = :_id")
    void setNewPassword(@Param("_password") String _password, @Param("_id") Long _id);

}
