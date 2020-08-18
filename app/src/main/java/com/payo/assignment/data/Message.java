package com.payo.assignment.data;

public class Message {
    private String senderId;
    private String body;
    private double amount;
    private TransactionType type;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public enum TransactionType {
        DEBIT,
        CREDIT,
        UNDEFINED
    }
}
