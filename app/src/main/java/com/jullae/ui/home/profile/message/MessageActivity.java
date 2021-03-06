package com.jullae.ui.home.profile.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jullae.R;
import com.jullae.data.db.model.MessageModel;
import com.jullae.ui.home.HomeActivity;
import com.jullae.utils.GlideUtils;

import java.util.List;

public class MessageActivity extends AppCompatActivity implements MessageView {

    private RecyclerView recyclerView;
    private MessagePresentor mPresentor;
    private String user_id, user_avatar;
    private MessageAdapter messageAdapter;
    private String currentUserId;
    private EditText addMessageField;
    private ImageView btn_add_message;
    private View progressBarMessage;
    private String user_name;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_message);

        if (getIntent() != null) {
            user_id = getIntent().getStringExtra("user_id");
            user_name = getIntent().getStringExtra("user_name");
            user_avatar = getIntent().getStringExtra("user_avatar");
        }
        mPresentor = new MessagePresentor();
        mPresentor.attachView(this);

        progressBar = findViewById(R.id.progress_bar);

        TextView title = findViewById(R.id.title);
        title.setText(user_name);
        currentUserId = mPresentor.getCurrentUserId();
        setUpRecyclerView();
        setupAddMessage();

        findViewById(R.id.tvClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GlideUtils.loadImagefromUrl(this, user_avatar, (ImageView) findViewById(R.id.user_photo));
        mPresentor.loadMessage(user_id);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MessageActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }


    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        messageAdapter = new MessageAdapter(this, currentUserId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        //     linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setAdapter(messageAdapter);
    }

    @Override
    public void onMessageListFetchSuccess(List<MessageModel> messageList) {
        if (messageList.size() > 0) {
            findViewById(R.id.empty).setVisibility(View.INVISIBLE);

            messageAdapter.add(messageList);
        } else {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.empty)).setText("No messages yet!");
        }
    }


    @Override
    public void onMessageListFetchFail() {

    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void setupAddMessage() {
        addMessageField = findViewById(R.id.field_add_message);
        btn_add_message = findViewById(R.id.btn_add_message);
        progressBarMessage = findViewById(R.id.progress_bar_message);
        btn_add_message.setEnabled(false);

        btn_add_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateCommentUI(0);
                mPresentor.sendMessageReq(addMessageField.getText().toString().trim(), user_id, new ReqListener() {
                    @Override
                    public void onSuccess(MessageModel messageModel) {
                        updateCommentUI(1);
                        messageAdapter.addMessage(messageModel);
                    }

                    @Override
                    public void onFail() {
                        updateCommentUI(2);
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


        addMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0) {
                    btn_add_message.setImageResource(R.drawable.ic_send_active_24dp);
                    btn_add_message.setEnabled(true);
                } else {
                    btn_add_message.setImageResource(R.drawable.ic_send_inactive_24dp);
                    btn_add_message.setEnabled(false);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCommentUI(int mode) {
        if (mode == 0) {
            progressBarMessage.setVisibility(View.VISIBLE);
            addMessageField.setEnabled(false);
            btn_add_message.setVisibility(View.INVISIBLE);
        } else if (mode == 1) {
            progressBarMessage.setVisibility(View.INVISIBLE);

            addMessageField.setText("");
            addMessageField.setEnabled(true);
            btn_add_message.setVisibility(View.VISIBLE);
        } else {
            progressBarMessage.setVisibility(View.INVISIBLE);

            addMessageField.setEnabled(true);
            btn_add_message.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        mPresentor.detachView();
        super.onDestroy();
    }

    public void scrollRecyclerView(int i) {
        recyclerView.scrollToPosition(i);
    }

    public interface ReqListener {
        void onSuccess(MessageModel messageModel);

        void onFail();
    }

}
