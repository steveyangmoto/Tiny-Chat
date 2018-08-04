package com.elitemobiletechnology.tinychat.model;

import org.json.JSONObject;

/**
 * Created by SteveYang on 17/1/22.
 */

public class ServerCommand extends TinyMessage {
    public static final String KEY_COMMAND = "command";
    public static final String KEY_SINCE = "since";
    public static final String COMMAND_HISTORY = "history";

    private String command;
    private long clientTime;
    private long since;

    public ServerCommand(String command,long clientTime,long since){
        this.command = command;
        this.clientTime = clientTime;
        this.since = since;
    }

    public String toJSONString(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_COMMAND, command);
            jsonObject.put(CLIENT_TIME_KEY,clientTime);
            jsonObject.put(KEY_SINCE,since);
        }catch (Exception ignore){}
        return jsonObject.toString();
    }
}
