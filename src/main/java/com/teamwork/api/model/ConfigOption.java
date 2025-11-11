package com.teamwork.api.model;

import java.util.List;

import com.teamwork.api.model.DTO.ConfigOptionDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "config_options")
public class ConfigOption {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    String name;

    @OneToMany(mappedBy = "option")
    List<OptionChoice> choices;

    public void updateFromDTO(ConfigOptionDTO dto) {
        this.name = dto.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
