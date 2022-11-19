package com.example.asyncmethod;

public class Dog {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String message;

    @Override
    public String toString() {
        return "Dog{" +
                "message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    private String status;
}
