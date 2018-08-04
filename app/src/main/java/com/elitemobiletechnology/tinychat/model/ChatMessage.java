package com.elitemobiletechnology.tinychat.model;

import com.elitemobiletechnology.tinychat.Util;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by SteveYang on 17/1/22.
 */

public class ChatMessage extends TinyMessage implements Serializable{
    public static final String MESSAGE_KEY = "msg";

    private String message;
    private long time;

    public ChatMessage(String message, long time){
        this.message = message;
        this.time = time;
    }

    public String getMessage(){
        return this.message;
    }

    public long getTime(){
        return this.time;
    }

    public String toJSONString(){
        JSONObject json = new JSONObject();
        try{
            json.put(MESSAGE_KEY,this.message);
            json.put(CLIENT_TIME_KEY,this.time);
        }catch (Exception ignore){}
        return json.toString();
    }

    public String getHumanReadableTime(){
        return Util.getHumanReadableTime(this.time);
    }
}
