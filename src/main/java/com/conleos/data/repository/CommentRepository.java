package com.conleos.data.repository;

import com.conleos.data.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("Select f from Comment f")
    List<Comment> getAllComments();
    @Query("Select f from Comment f where f.form.id = :formId")
    List<Comment> getCommentsByFormId(@Param("formId") Long formId);

}
