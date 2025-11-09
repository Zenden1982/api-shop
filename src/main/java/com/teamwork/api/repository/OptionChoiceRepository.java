package com.teamwork.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamwork.api.model.OptionChoice;

@Repository
public interface OptionChoiceRepository extends JpaRepository<OptionChoice, Long> {

}
