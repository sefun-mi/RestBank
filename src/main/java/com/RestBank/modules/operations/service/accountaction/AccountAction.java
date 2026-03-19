package com.RestBank.modules.operations.service.accountaction;

import com.RestBank.modules.data.enums.TransactionType;

public interface AccountAction {
    TransactionType getType();
    void effect(Object argument);
}
