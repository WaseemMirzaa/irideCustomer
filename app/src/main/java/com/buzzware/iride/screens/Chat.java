package com.buzzware.iride.screens;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iride.FirebaseRequest.FirebaseRequests;
import com.buzzware.iride.adapters.ConversationAdapter;
import com.buzzware.iride.databinding.FragmentChatBinding;
import com.buzzware.iride.models.ConversationModel;
import com.buzzware.iride.models.LastMessageModel;
import com.buzzware.iride.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Chat extends BaseActivity {

    FragmentChatBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    List<LastMessageModel> lastMessages;

    FirebaseRequests firebaseRequests;

    public Chat() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();

        setListener();

        getList();

    }

    private void init() {

        firebaseRequests = new FirebaseRequests();

    }

    private void getList() {

        firebaseRequests.GetConversationList((list, isError, message) -> {

            if (!isError) {

                lastMessages = list;

                getUsersList();

            } else {

                showErrorAlert(message);

            }

        }, mAuth.getCurrentUser().getUid(), Chat.this);

    }

    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> finish());

    }

    List<ConversationModel> conversations = new ArrayList<>();

    void getUsersList() {

        conversations.clear();

        FirebaseInstances.usersCollection
                . get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()) {

                            User user = documentSnapshot.toObject(User.class);

                            user.id = documentSnapshot.getId();

                            for(int i = 0; i< lastMessages.size(); i++) {

                                String otherUserId = lastMessages.get(i).fromID;

                                if(!getUserId().equalsIgnoreCase(lastMessages.get(i).fromID))

                                    otherUserId = lastMessages.get(i).toID;


                                if(user.id.equalsIgnoreCase(otherUserId)) {

                                    ConversationModel conversation = getConversationModel(lastMessages.get(i),user);

                                    conversations.add(conversation);
                                }

                            }

                        }

                    }

                    setConversations(conversations);

                });

    }

    private ConversationModel getConversationModel(LastMessageModel lastMessageModel, User user) {

        ConversationModel model = new ConversationModel();

        model.conversationID = lastMessageModel.conversationId;
        model.name = user.firstName+" "+user.lastName;
        model.image = user.image;
        model.lastMessage = lastMessageModel.content;
        model.id = lastMessageModel.conversationId;
        model.toID = lastMessageModel.toID;

        return model;

    }

    public void setConversations(List<ConversationModel> list) {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(Chat.this));

        ConversationAdapter conversationAdapter = new ConversationAdapter(Chat.this, list, listener);

        binding.rvMessages.setAdapter(conversationAdapter);

        conversationAdapter.notifyDataSetChanged();

    }

    ConversationAdapter.OnClickListener listener = conversationModel -> {

        Intent intent = new Intent(Chat.this, MessagesActivity.class);

        intent.putExtra("conversationID", conversationModel.getConversationID());
        intent.putExtra("selectedUserID", conversationModel.toID);
        intent.putExtra("selectedUserName", conversationModel.getName());
        intent.putExtra("checkFrom", "false");

        startActivity(intent);

    };

}