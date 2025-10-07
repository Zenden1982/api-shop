package com.teamwork.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teamwork.api.model.User;
import com.teamwork.api.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers(int page, int size) {
        return userRepository.findAll(); // Simplified for brevity; implement pagination logic as needed
    }
}
