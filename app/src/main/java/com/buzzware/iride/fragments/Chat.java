package com.buzzware.iride.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iride.FirebaseRequest.FirebaseRequests;
import com.buzzware.iride.adapters.ConversationAdapter;
import com.buzzware.iride.databinding.FragmentChatBinding;
import com.buzzware.iride.models.ConversationModel;
import com.buzzware.iride.models.User;
import com.buzzware.iride.screens.BaseActivity;
import com.buzzware.iride.screens.MessagesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {

    FragmentChatBinding binding;
    
    List<User> userList=new ArrayList<>();
    
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public Chat() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListener();

        getList();

    }

    private void getList() {

        FirebaseRequests.GetFirebaseRequests(Chat.this).GetConversationList(callback, mAuth.getCurrentUser().getUid(), Chat.this);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    ConversationResponseCallback callback= new ConversationResponseCallback() {
        @Override
        public void onResponse(List<ConversationModel> list, boolean isError, String message) {
            if(!isError){

                SetConversationList(list);
                Log.e("zcc", "NotEmpty");
            }else{

                Log.e("zcc", "Empty");

            }
        }
    };

    public void SetConversationList(List<ConversationModel> list){

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(Chat.this));
        ConversationAdapter conversationAdapter= new ConversationAdapter(Chat.this, list, listener);
        binding.rvMessages.setAdapter(conversationAdapter);
        conversationAdapter.notifyDataSetChanged();

    }

    ConversationAdapter.OnClickListener listener= new ConversationAdapter.OnClickListener() {
        @Override
        public void onClick(ConversationModel conversationModel) {
            Intent intent= new Intent(Chat.this, MessagesActivity.class);
            intent.putExtra("conversationID", conversationModel.getConversationID());
            intent.putExtra("selectedUserID", conversationModel.getId());
            intent.putExtra("selectedUserName", conversationModel.getName());
            intent.putExtra("checkFrom", "false");
            startActivity(intent);
        }
    };

}