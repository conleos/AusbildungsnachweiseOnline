package com.conleos.data.service;

import com.conleos.data.entity.Comment;
import com.conleos.data.entity.Form;
import com.conleos.data.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private static CommentService instance;

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        instance = this;
        this.commentRepository = commentRepository;
    }

    public static CommentService getInstance() {
        return instance;
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsByForm(long id) {
        return commentRepository.getCommentsByFormId(id);
    }

    public void deleteCommentsByForm(long id) {
        List<Comment> comments = getAllCommentsByForm(id);
        commentRepository.deleteAll(comments);
    }

}
