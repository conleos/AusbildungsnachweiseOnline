package com.conleos.data.service;

import com.conleos.data.entity.User;
import com.conleos.data.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static UserService instance;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        instance = this;
        this.userRepository = userRepository;
    }

    public static UserService getInstance() {
        return instance;
    }

    public List<Integer> getAllUser() {
        return null;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

}
