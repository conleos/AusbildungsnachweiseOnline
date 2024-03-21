package com.conleos.data.service;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
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

        // By default, if there is no User an Admin user is created!
        if (isUserbaseEmpty()) {
            createUser(new User("admin", PasswordHasher.hash("1234"), Role.Admin));
        }
    }

    public static UserService getInstance() {
        return instance;
    }

    public List<Integer> getAllUserIDs() {
        return userRepository.getAllUsers();
    }

    public boolean isUserbaseEmpty() {
        return getAllUserIDs().size() == 0;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }
    public User getUserByID(Long id) {
        List<User> temp = userRepository.getUserByChannel(id);
        return temp.isEmpty() ? null : temp.getFirst();
    }
    public User getUserByUsername(String username) {
        List<User> temp = userRepository.getUserByName(username);
        return temp.isEmpty() ? null : temp.getFirst();
    }
}
