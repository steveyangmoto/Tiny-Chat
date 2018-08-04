package com.elitemobiletechnology.tinychat.view;

import com.elitemobiletechnology.tinychat.model.ChatMessage;

/**
 * Created by SteveYang on 17/1/21.
 */

public interface MainView {
    void onMessageReceived(ChatMessage chatMessage);
    void onMessageNotSent(ChatMessage chatMessage);
}
