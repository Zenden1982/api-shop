package com.teamwork.api.model.YooKassaModel;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YooKassaReceipt {
    private YooKassaCustomer customer;
    private List<YooKassaReceiptItem> items;
}
