package com.RestBank.modules.common.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class WebResponseBuilder {
    public static ResponseEntity<Object> buildSuccessResponse(Object data){
        WebResponse webResponse = new WebResponse("00",true,"Successful",data);
        return ResponseEntity.ok(webResponse);
    }

    public static ResponseEntity<Object> buildFailureResponse(String message, HttpStatusCode httpStatusCode){
        WebResponse webResponse = new WebResponse("06",false, message,null);
        return new ResponseEntity<>(webResponse,httpStatusCode);
    }

    public static ResponseEntity<Object> buildResponse(String message, boolean success,Object data, HttpStatusCode httpStatusCode){
        String responseCode = success ? "00" : "06";
        WebResponse webResponse = new WebResponse(responseCode, success, message, data);
        return new ResponseEntity<>(webResponse,httpStatusCode);
    }
}