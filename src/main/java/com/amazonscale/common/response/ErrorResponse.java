package com.amazonscale.common.response;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(){}

    public ErrorResponse(LocalDateTime timestamp,
                         int status,
                         String error,
                         String message,
                         String path){
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // GETTER AND SETTER FOR TIMESTAMP
    public LocalDateTime getTimestamp(){
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

    // GETTER AND SETTER FOR STATUS
    public int getStatus(){
        return status;
    }
    public void setStatus(int status){
        this.status = status;
    }

    //GETTER AND SETTER FOR ERROR
    public String getError(){
        return error;
    }
    public void setError(String error){
        this.error = error;
    }

    //GETTER AND SETTER FOR MESSAGE
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    //GETTER AND SETTER FOR PATH
    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

}
