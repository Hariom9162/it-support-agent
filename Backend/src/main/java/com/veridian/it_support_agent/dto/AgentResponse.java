package com.veridian.it_support_agent.dto;

public class AgentResponse {

    private String answer;
    private String status;
    private String source;
    private String ticketId;
    private boolean escalated;

    public AgentResponse() {
    }

    public AgentResponse(String answer,
                         String status,
                         String source,
                         String ticketId,
                         boolean escalated) {
        this.answer = answer;
        this.status = status;
        this.source = source;
        this.ticketId = ticketId;
        this.escalated = escalated;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }
}