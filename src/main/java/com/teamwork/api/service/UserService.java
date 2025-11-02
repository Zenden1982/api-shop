package com.teamwork.api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamwork.api.config.Security.JwtTokenUtils;
import com.teamwork.api.model.AuthRequest;
import com.teamwork.api.model.DTO.UserCreateUpdateDTO;
import com.teamwork.api.model.DTO.UserReadDTO;
import com.teamwork.api.model.Role;
import com.teamwork.api.model.User;
import com.teamwork.api.repository.RoleRepository;
import com.teamwork.api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public UserReadDTO createUser(UserCreateUpdateDTO userDTO) {
        User user = UserCreateUpdateDTO.toUser(userDTO);
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

        // Установка роли по умолчанию
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(0, "ROLE_USER")));
        user.setRoles(List.of(userRole));

        userRepository.save(user);
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public Page<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public UserReadDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public UserReadDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public UserReadDTO updateUser(Long id, UserCreateUpdateDTO userDTO) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Обновляем только переданные поля (partial update)
        if (userDTO.getUsername() != null && !userDTO.getUsername().isBlank()) {
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getFirstName() != null && !userDTO.getFirstName().isBlank()) {
            user.setFirstName(userDTO.getFirstName());
        }

        if (userDTO.getLastName() != null && !userDTO.getLastName().isBlank()) {
            user.setLastName(userDTO.getLastName());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isBlank()) {
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);

        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }

    public String generateToken(AuthRequest user, AuthenticationManager authenticationManager) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (AuthenticationException e) {
            return e.getMessage();
        }

        UserDetails userDetails = loadUserByUsername(user.getUsername());
        return jwtTokenUtils.generateToken(userDetails);
    }

}