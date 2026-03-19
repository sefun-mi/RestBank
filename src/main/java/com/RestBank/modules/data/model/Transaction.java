package com.RestBank.modules.data.model;

import com.RestBank.modules.data.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private LocalDateTime transactionDate;
    private TransactionType transactionType;
    private String narration;
    private Double amount;
    private Double accountBalance;
    private String reference;
}
