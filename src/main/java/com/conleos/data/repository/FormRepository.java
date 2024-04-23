package com.conleos.data.repository;

import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FormRepository extends JpaRepository<Form, Long> {

    @Query("Select f from Form f")
    List<Form> getAllForms();
    @Query("Select distinct f.id from Form f")
    List<Integer> getAllFormChannels();
    @Query("Select f from Form f where f.id = :_id")
    List<Form> getFormByChannel(@Param("_id") Long _id);

    @Query ("Select f from Form f where f.owner = :_owner order by f.mondayOfThatWeek")
    List<Form> getFormsByOwner(@Param("_owner") User _owner);

    @Query ("Select f from Form f where f.mondayOfThatWeek = :_date and f.owner = :_user")
    List<Form> getFormsByDateAndUser(@Param("_date") LocalDate _date, @Param("_user") User _user);

}
