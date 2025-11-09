package com.teamwork.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "option_choice")
public class OptionChoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String value;

    BigDecimal price;

    @ManyToOne
    ConfigOption option;
}
