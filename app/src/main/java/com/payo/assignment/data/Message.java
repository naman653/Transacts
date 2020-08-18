package com.payo.assignment.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Message")
public class Message {
    @PrimaryKey
    @NonNull
    private String msgId;
    private String senderId;
    private String body;
    private double amount;
    private String type;

    @NonNull
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(@NonNull String msgId) {
        this.msgId = msgId;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
