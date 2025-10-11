package com.teamwork.api.model.YooKassaModel;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YooKassaPaymentRequest {
    private YooKassaAmount amount;
    private Boolean capture;
    private YooKassaConfirmation confirmation;
    private String description;
    private Map<String, String> metadata;
    private YooKassaReceipt receipt;
}
