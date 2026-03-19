package com.RestBank.modules.operations.api;

import com.RestBank.modules.data.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionHistoryResponse{
    private String transactionDate;
    private TransactionType transactionType;
    private String narration;
    private Double amount;
    private Double accountBalance;
}
