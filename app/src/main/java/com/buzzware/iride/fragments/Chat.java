package com.buzzware.iride.fragments;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.adapters.MessageAddapter;
import com.buzzware.iride.models.ChatModel;
import com.buzzware.iride.databinding.FragmentChatBinding;
import com.buzzware.iride.screens.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {

    FragmentChatBinding mBinding;
    MessageAddapter messageAddapter;
    List<ChatModel> messageModels;

    public Chat() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = FragmentChatBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        mBinding.drawerIcon.setOnClickListener(v -> finish());

        Init();
    }



    private void Init() {
        messageModels= new ArrayList<>();
        SetDummyList();
        mBinding.rvMessages.setLayoutManager(new LinearLayoutManager(Chat.this));
        messageAddapter= new MessageAddapter(this, messageModels);
        mBinding.rvMessages.setAdapter(messageAddapter);
        messageAddapter.notifyDataSetChanged();
    }

    private void SetDummyList() {
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
        messageModels.add(new ChatModel());
    }
}