package com.example.laris.testregistration.HelperClasses;

import java.util.Date;

/**
 * Created by laris on 11/22/2017.
 */

public class ChatRoom {
    private String Rname;
    private String user;
    private long now;

    public ChatRoom(String Rname, String user)
    {
        this.Rname = Rname;
        this.user = user;
        now = new Date().getTime();
    }
    public ChatRoom(){
    }
    public String getRname() {
        return Rname;
    }

    public void setRname(String rname) {
        Rname = rname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }
}
