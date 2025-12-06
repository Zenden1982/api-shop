package com.teamwork.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.teamwork.api.config.Security.JwtTokenUtils;
import com.teamwork.api.exception.ResourceNotFoundException;
import com.teamwork.api.exception.UserAlreadyExistsException;
import com.teamwork.api.model.AuthRequest;
import com.teamwork.api.model.Cart;
import com.teamwork.api.model.Role;
import com.teamwork.api.model.User;
import com.teamwork.api.model.DTO.UserCreateUpdateDTO;
import com.teamwork.api.model.DTO.UserReadDTO;
import com.teamwork.api.repository.CartRepository;
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

    private final CartRepository cartRepository;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public UserReadDTO createUser(UserCreateUpdateDTO userDTO) {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
        User user = UserCreateUpdateDTO.toUser(userDTO);
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));

        // Установка роли по умолчанию
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role(0, "USER")));
        user.setRoles(List.of(userRole));

        User savedUser = userRepository.save(user);
        Cart newUserCart = new Cart();
        newUserCart.setUser(savedUser); // Привязываем корзину к только что созданному пользователю
        cartRepository.save(newUserCart);
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public UserReadDTO findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public UserReadDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден id: " + id));
        return UserReadDTO.toUserReadDTO(user);
    }

    @Transactional
    public void assignRolesToUser(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Роль " + roleName + " не найдена")))
                .collect(Collectors.toList());

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    public UserReadDTO updateUser(Long id, UserCreateUpdateDTO userDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        // Обновляем только переданные поля (partial update)
        if (userDTO.getUsername() != null && !userDTO.getUsername().isBlank()) {
            // Хорошей практикой будет проверка на уникальность нового имени, если оно
            // меняется
            if (!user.getUsername().equals(userDTO.getUsername())
                    && userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                throw new UserAlreadyExistsException("Имя пользователя " + userDTO.getUsername() + " уже занято");
            }
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
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