package com.veridian.it_support_agent.dto;

public class AgentRequest {

    private String employeeName;
    private String message;

    public AgentRequest() {
    }

    public AgentRequest(String employeeName, String message) {
        this.employeeName = employeeName;
        this.message = message;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}