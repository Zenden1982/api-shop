package com.teamwork.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.teamwork.api.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
