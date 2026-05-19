package com.RestBank.modules.common.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class WebResponseBuilder {
    public static ResponseEntity<Object> buildSuccessResponse(Object data){
        WebResponseBody webResponseBody = new WebResponseBody("00",true,"Successful",data);
        return ResponseEntity.ok(webResponseBody);
    }

    public static ResponseEntity<Object> buildFailureResponse(String message, HttpStatusCode httpStatusCode){
        WebResponseBody webResponseBody = new WebResponseBody("06",false, message,null);
        return new ResponseEntity<>(webResponseBody,httpStatusCode);
    }

    public static ResponseEntity<Object> buildResponse(String message, boolean success,Object data, HttpStatusCode httpStatusCode){
        String responseCode = success ? "00" : "06";
        WebResponseBody webResponseBody = new WebResponseBody(responseCode, success, message, data);
        return new ResponseEntity<>(webResponseBody,httpStatusCode);
    }
}