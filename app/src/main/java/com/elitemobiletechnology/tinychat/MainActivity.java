package com.elitemobiletechnology.tinychat;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elitemobiletechnology.tinychat.model.ChatMessage;
import com.elitemobiletechnology.tinychat.presenter.MainPresenter;
import com.elitemobiletechnology.tinychat.presenter.MainPresenterImpl;
import com.elitemobiletechnology.tinychat.view.MainView;

/**
 * @author SteveYang
 */
public class MainActivity extends Activity implements MainView{

    Button b_send;
    EditText et_message;
    TextView tv_response;
    MainPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenterImpl(this);
        b_send = (Button) findViewById(R.id.b_send);
        et_message = (EditText) findViewById(R.id.et_message);
        tv_response = (TextView) findViewById(R.id.tv_response);

        tv_response.setMovementMethod(new ScrollingMovementMethod());

        b_send.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                presenter.sendMessage(et_message.getText().toString());
                et_message.setText("");
            }
        });
    }

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        if(chatMessage!=null) {
            String myMessage = chatMessage.getHumanReadableTime() + ": " + chatMessage.getMessage() + "\n";
            tv_response.setText(myMessage + tv_response.getText());
        }
    }

    @Override
    public void onMessageNotSent(ChatMessage chatMessage) {
        if(chatMessage!=null) {
            String myMessage = "\"" + chatMessage.getMessage() + "\"" + this.getString(R.string.cannot_send_message) + "\n";
            tv_response.setText(myMessage + tv_response.getText());
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.destroy();
    }
}