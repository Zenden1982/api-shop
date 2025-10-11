package com.teamwork.api.model.YooKassaModel;

import lombok.Data;

@Data
public class YooKassaPaymentResponse {
    private String id;
    private String status;
    private YooKassaAmount amount;
    private YooKassaConfirmation confirmation;
}