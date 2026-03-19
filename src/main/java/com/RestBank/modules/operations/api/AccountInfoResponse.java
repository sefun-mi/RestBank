package com.RestBank.modules.operations.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfoResponse {
    private String accountName;
    private String accountNumber;
    private Double balance;
}
