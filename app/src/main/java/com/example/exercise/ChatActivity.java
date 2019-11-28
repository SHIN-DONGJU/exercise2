package com.example.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.speech.tts.TextToSpeech.ERROR;
import android.speech.tts.TextToSpeech; //tts 관련 함수 import

import java.util.Locale;    //tts 관련 함수 import
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    public  RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ChatData> chatList;

    private String nick;
    public String type;

    private EditText EditText_chat;
    private Button Button_send;
    private DatabaseReference myRef;

    private TextToSpeech tts;   //tts 함수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent =getIntent();
        nick=intent.getExtras().getString("nickname");
        type=intent.getExtras().getString("type");

        Button_send = findViewById(R.id.Button_send);
        EditText_chat = findViewById(R.id.EditText_chat);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR) {
                    //언어 선택
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        }); // TTS 생성 후, OnInitListener로 초기화

        Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = EditText_chat.getText().toString(); //msg

                tts.speak(EditText_chat.getText().toString(),TextToSpeech.QUEUE_FLUSH, null);    //edittext_chat 내의 문장을 읽음

                if(msg != null) {
                    ChatData chat = new ChatData();
                    chat.setNickname(nick);
                    chat.setMsg(msg);
                    myRef.push().setValue(chat);
                }
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList, ChatActivity.this, nick,type);

        mRecyclerView.setAdapter(mAdapter);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef =database.getReference("message");

        //caution!!!
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatData chat = dataSnapshot.getValue(ChatData.class);
                ((ChatAdapter) mAdapter).addChat(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //1. recyclerView - 반복 채팅창!
        // chat data : message, nickname, isMine -> data transfer object
        //2. db 내용을 넣는다.
        //3. 상대방 폰에 채팅 내용이 보임.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TTS 객체가 남아있을 경우, 실행을 중지하고 메모리에서 제거
        if(tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
