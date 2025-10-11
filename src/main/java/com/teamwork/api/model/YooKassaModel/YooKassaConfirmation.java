package com.teamwork.api.model.YooKassaModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YooKassaConfirmation {
    private String type;
    @JsonProperty("return_url")
    private String returnUrl;
}
