package com.elitemobiletechnology.tinychat.presenter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.elitemobiletechnology.tinychat.Constants;
import com.elitemobiletechnology.tinychat.MyApplicaton;
import com.elitemobiletechnology.tinychat.PersistentQueue;
import com.elitemobiletechnology.tinychat.Util;
import com.elitemobiletechnology.tinychat.model.ChatMessage;
import com.elitemobiletechnology.tinychat.model.ServerCommand;
import com.elitemobiletechnology.tinychat.view.MainView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by SteveYang on 17/1/21.
 */

public class MainPresenterImpl implements MainPresenter, Handler.Callback {
    private static final String TAG = "MainPresenterImpl";
    private static final String TIME_RECEIVING_LAST_MESSAGE = "time_receiving_last_message";
    private static final String PERSISTENT_STORAGE_FILE_NAME = "ChatMessageQueue";
    private static final int TINY_CHAT_MESSAGE = 123456;
    private static final int SERVER_COMMAND_MESSAGE = 123457;
    private static final int RECONNECT_INTERVAL_MILLIS = 3000;
    private PersistentQueue sendMessageQueue;
    private HandlerThread senderThread;
    private HandlerThread receiverThread;
    private Handler senderHandler;
    private Handler receiverHandler;
    private Handler mainHandler;
    private MainView mainView;
    private Socket socket;

    private Runnable messageReceiverTask = new Runnable() {
        @Override
        public void run() {
           String s ="";
            BufferedReader bufferedReader = null;
            try {
                socket = new Socket(Constants.destinationIpAddress, Constants.destinationPortNumber);
                bufferedReader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                long lastOfflineTime = Util.getPrefValue(TIME_RECEIVING_LAST_MESSAGE);
                if (lastOfflineTime > 0) {
                    long currentTime = System.currentTimeMillis();
                    ServerCommand serverCommand = new ServerCommand(ServerCommand.COMMAND_HISTORY, currentTime, lastOfflineTime);
                    sendCommand(serverCommand);
                }
                while (sendMessageQueue.getSize() > 0) {
                    ChatMessage message = (ChatMessage) sendMessageQueue.dequeue();
                    sendMessage(message);
                }
                while (true) {
                    String lineReceived = bufferedReader.readLine();
                    Util.savePrefValue(TIME_RECEIVING_LAST_MESSAGE, System.currentTimeMillis());
                    try {
                        JSONObject json = new JSONObject(lineReceived);
                        final String message = json.getString(ChatMessage.MESSAGE_KEY);
                        final long time = json.getLong(ChatMessage.SERVER_TIME_KEY);
                        if (mainHandler != null) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mainView.onMessageReceived(new ChatMessage(message, time));
                                }
                            });
                        }
                    } catch (Exception ignore) {
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (Exception ignore) {
                }
            }
            if (receiverHandler != null) {
                receiverHandler.postDelayed(this, RECONNECT_INTERVAL_MILLIS);
            }
        }
    };

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        sendMessageQueue = new PersistentQueue<>(MyApplicaton.getAppContext(), PERSISTENT_STORAGE_FILE_NAME);
        senderThread = new HandlerThread("sender");
        senderThread.start();
        receiverThread = new HandlerThread("receiver");
        receiverThread.start();
        mainHandler = new Handler(Looper.getMainLooper());
        senderHandler = new Handler(senderThread.getLooper(), this);
        receiverHandler = new Handler(receiverThread.getLooper());
        receiverHandler.post(messageReceiverTask);
    }


    @Override
    public void sendMessage(String msg) {
        ChatMessage chatMessage = new ChatMessage(msg, System.currentTimeMillis());
        sendMessage(chatMessage);
    }

    private void sendCommand(ServerCommand command) {
        if (senderHandler != null) {
            Message msg = new Message();
            msg.what = SERVER_COMMAND_MESSAGE;
            msg.obj = command;
            senderHandler.sendMessage(msg);
        }
    }

    private void sendMessage(ChatMessage chatMessage) {
        if (senderHandler != null) {
            Message message = new Message();
            message.what = TINY_CHAT_MESSAGE;
            message.obj = chatMessage;
            senderHandler.sendMessage(message);
        }
    }

    @Override
    public void destroy() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception ignore) {
            }
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        if (senderHandler != null) {
            senderHandler.removeCallbacksAndMessages(null);
        }
        if (receiverHandler != null) {
            receiverHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == TINY_CHAT_MESSAGE) {
            if (socket == null || !socket.isConnected()) {
                final ChatMessage chatMessage = (ChatMessage) (msg.obj);
                sendMessageQueue.enqueue(chatMessage);
                if (mainHandler != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainView.onMessageNotSent(chatMessage);
                        }
                    });
                }
                return true;
            }
            try {
                ChatMessage chatMessage = (ChatMessage) msg.obj;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(chatMessage.toJSONString());
                return true;
            } catch (Exception e) {
                Log.e(TAG, "exception: " + e.getMessage());
            }
        } else if (msg.what == SERVER_COMMAND_MESSAGE) {
            if (socket == null || !socket.isConnected()) {
                return true;
            }
            try {
                ServerCommand serverCommand = (ServerCommand) msg.obj;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(serverCommand.toJSONString());
                return true;
            } catch (Exception e) {
                Log.e(TAG, "exception: " + e.getMessage());
            }
        }
        return false;
    }
}
