package com.github.afikrim.flop.utils.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

public class Response<T> extends RepresentationModel<Response<T>> implements Serializable {

    private static final long serialVersionUID = 3943340579706180715L;

    private Boolean success = true;
    private ResponseCode responseCode = ResponseCode.HTTP_OK;
    private String message;
    private T data;

    public Response() {
    }

    public Response(Boolean success, ResponseCode responseCode, String message, T data) {
        this.success = success;
        this.responseCode = responseCode;
        this.message = message;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("code")
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
