package com.RestBank.modules.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebResponse {
    private String responseCode;
    private boolean success;
    private String message;
    private Object data;
}