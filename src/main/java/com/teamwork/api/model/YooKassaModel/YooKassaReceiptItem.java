package com.teamwork.api.model.YooKassaModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YooKassaReceiptItem {
    private String description;
    private String quantity;
    private YooKassaAmount amount;
    @JsonProperty("vat_code")
    private Integer vatCode;
    @JsonProperty("payment_mode")
    private String paymentMode;
    @JsonProperty("payment_subject")
    private String paymentSubject;
}
