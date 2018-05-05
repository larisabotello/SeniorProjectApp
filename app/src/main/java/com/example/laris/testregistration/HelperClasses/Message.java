package com.example.laris.testregistration.HelperClasses;

/**
 * Created by laris on 11/4/2017.
 */

import java.util.Date;

public class Message {
    private String msgText;
    private String msgUser;
    private long msgTime;

    public Message(String msgText, String msgUser) {
        this.msgText = msgText;
        this.msgUser = msgUser;

        msgTime = new Date().getTime();
    }

    public Message() {
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgUser() {
        return msgUser;
    }

    public void setMsgUser(String msgUser) {
        this.msgUser = msgUser;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
