package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamwork.api.model.ConfigOption;

@Repository
public interface ConfigOptionRepository extends JpaRepository<ConfigOption, Long> {

}
