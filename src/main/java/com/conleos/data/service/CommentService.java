package com.conleos.data.service;

import com.conleos.data.entity.Comment;
import com.conleos.data.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private static CommentService instance;

    private final CommentRepository cRepository;

    public CommentService(CommentRepository cRepository) {
        instance = this;
        this.cRepository = cRepository;
    }

    public static CommentService getInstance() {
        return instance;
    }

    public void saveComment(Comment c) {
        cRepository.save(c);
    }

    public List<Comment> getAllCommentsByForm(long id) {
        return cRepository.getCommentsByFormId(id);
    }
}
