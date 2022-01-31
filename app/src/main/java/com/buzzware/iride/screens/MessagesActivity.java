package com.buzzware.iride.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.FirebaseRequest.ConversationResponseCallback;
import com.buzzware.iride.FirebaseRequest.FirebaseRequests;
import com.buzzware.iride.FirebaseRequest.MessagesResponseCallback;
import com.buzzware.iride.adapters.MessagesAdapter;
import com.buzzware.iride.adapters.RideType;
import com.buzzware.iride.databinding.ActivityMessagesBinding;
import com.buzzware.iride.models.LastMessageModel;
import com.buzzware.iride.models.MessageModel;
import com.buzzware.iride.models.RideModel;
import com.buzzware.iride.models.SendConversationModel;
import com.buzzware.iride.models.SendLastMessageModel;
import com.buzzware.iride.models.User;
import com.buzzware.iride.utils.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class MessagesActivity extends AppCompatActivity {


    ActivityMessagesBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String conversationID = "";

    String selectedUserName = "";

    String selectedUserId = "";

    String currentUserId = "";

    String isFromNew = "false";

    String myImageUrl;

    String otherUserImageUrl;

    String rideID;

    FirebaseRequests firebaseRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMessagesBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        init();

        getDataFromExtra();

        setListener();

        setListenerOnRide();

    }

    private void init() {

        firebaseRequests = new FirebaseRequests();

    }

    public void getMyImage() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(currentUserId);

        reference
                .get()
                .addOnCompleteListener(task -> {

                    myImageUrl = "";

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            myImageUrl = user.image;

                        }

                    }

                    getOtherUserImage();

                });
    }

    public void getOtherUserImage() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(selectedUserId);

        reference
                .get()
                .addOnCompleteListener(task -> {

                    otherUserImageUrl = "";

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            otherUserImageUrl = user.image;

                        }

                    }

                    loadMessages();


                });

    }

    private void setListener() {

        binding.sendBtn.setOnClickListener(v -> {

            if (!binding.messageET.getText().toString().isEmpty()) {

                sendMessage();

            }
        });

        binding.drawerIcon.setOnClickListener(v -> finish());

    }


    private void getDataFromExtra() {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            isFromNew = getIntent().getStringExtra("checkFrom");

            currentUserId = mAuth.getCurrentUser().getUid();

            if (isFromNew.equals("false") || isFromNew.equals("admin")) {

                conversationID = getIntent().getStringExtra("conversationID");

                getConversation(getIntent().getStringExtra("conversationID"));

            } else {

                selectedUserId = getIntent().getStringExtra("selectedUserID");

                selectedUserName = getIntent().getStringExtra("selectedUserName");

                binding.tvTitle.setText(selectedUserName);

                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                conversationID = UUID.randomUUID().toString();

                checkAlreadyHaveChatOrNot();

            }

        }

    }

    private void getConversation(String conversationID) {

        FirebaseInstances.chatCollection.document(conversationID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null) {

                        if (task.getResult() != null) {

                            LastMessageModel lastMessageModel = task.getResult().get("lastMessage", LastMessageModel.class);

                            if (lastMessageModel != null) {

                                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                selectedUserId = lastMessageModel.fromID;

                                if (currentUserId.equalsIgnoreCase(lastMessageModel.fromID)) {

                                    selectedUserId = lastMessageModel.toID;

                                }

                                this.conversationID = conversationID;

                                getMyImage();

                            }

                        }


                    }
                });

    }

    private void checkAlreadyHaveChatOrNot() {

        getListToCheck();

    }

    private void getListToCheck() {

        firebaseRequests.GetConversationList(callbackCheck, mAuth.getCurrentUser().getUid(), MessagesActivity.this);

    }

    ConversationResponseCallback callbackCheck = (list, isError, message) -> {

        if (!isError) {

            if (list.size() > 0) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).getToID().equals(selectedUserId) || list.get(i).getFromID().equals(selectedUserId)) {

                        conversationID = list.get(i).conversationId;

                        isFromNew = "false";

                        getMyImage();

                        return;
                    }
                }
            }

            Log.e("data", "NotEmpty");

        } else {

            Log.e("data", "Empty");

        }
    };

    private void loadMessages() {

        if (isFromNew.equals("admin")) {

            firebaseRequests.LoadAdminMessages(callback, MessagesActivity.this, conversationID);

        } else {

            firebaseRequests.LoadMessages(callback, MessagesActivity.this, conversationID);

        }

    }

    MessagesResponseCallback callback = (list, isError, message) -> {

        if (list.size() == 0 || isError) {

//            if (rideID != null) {
//
//                setListenerOnRide();
//
//                return;
//
//            }

        }
        if (!isError) {

            Sorting(list);

        }
    };

    ListenerRegistration eventListener;

    AlertDialog messagesDeletedPopup;

    private void setListenerOnRide() {

        eventListener = FirebaseInstances.bookingsCollection.document(conversationID)
                .addSnapshotListener((value, error) -> {

                    if (value != null) {

                        RideModel rideModel = value.toObject(RideModel.class);

                        if (rideModel != null) {

                            rideModel.id = value.getId();

                            if (AppConstants.RideStatus.RE_BOOKED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.CANCELLED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.RIDE_COMPLETED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.RATED.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.DISPUTE.equalsIgnoreCase(rideModel.status) ||
                                    AppConstants.RideStatus.DISPUTED.equalsIgnoreCase(rideModel.status)
                            ) {

                                eventListener.remove();

                                eventListener = null;

                                if (messagesDeletedPopup != null && messagesDeletedPopup.isShowing())

                                    return;
//                        if(value.getData().to)
                                messagesDeletedPopup = new AlertDialog.Builder(MessagesActivity.this)
                                        .setCancelable(false)
                                        .setTitle("Alert")
                                        .setMessage("Ride is completed or cancelled. This chat has been deleted")
                                        .setPositiveButton("OK", (dialogInterface, i) -> {

                                            dialogInterface.dismiss();
                                            finish();

                                        }).create();
                                messagesDeletedPopup.show();

                            }
                        }
                    }
                });

    }

    private void Sorting(List<MessageModel> list) {

        Collections.sort(list, (o1, o2) -> Long.compare(o1.getTimestamp(), o2.getTimestamp()));

        ///set data
        SetRecyclerView(list);
    }

    public void SetRecyclerView(List<MessageModel> list) {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(MessagesActivity.this));

        MessagesAdapter messagesAdapter = new MessagesAdapter(MessagesActivity.this, list, currentUserId, myImageUrl, otherUserImageUrl);

        binding.rvMessages.setAdapter(messagesAdapter);

        binding.rvMessages.scrollToPosition(list.size() - 1);

    }


    private void sendMessage() {

        if (isFromNew.equals("true")) {

            long currentTimeStamp = System.currentTimeMillis();

            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),

                    currentUserId, String.valueOf(currentTimeStamp), selectedUserId, "text", false, (int) currentTimeStamp);

            HashMap<String, Boolean> participents = new HashMap<>();

            participents.put(currentUserId, true);

            participents.put(selectedUserId, true);

            SendConversationModel sendConversationModel = new SendConversationModel(binding.messageET.getText().toString(),
                    currentUserId, String.valueOf(currentTimeStamp), "text", false, currentTimeStamp, selectedUserId);

            HashMap<String, Object> lasthashMap = new HashMap<>();

            lasthashMap.put("lastMessage", sendLastMessageModel);

            lasthashMap.put("participants", participents);

            conversationID = UUID.randomUUID().toString();

            firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chat").document(conversationID).set(lasthashMap);

        } else {
            SendAlreadyExist();
        }

        loadMessages();
        checkAlreadyHaveChatOrNot();

        binding.messageET.setText("");
    }

    public void SendAlreadyExist() {

        long currentTimeStamp = System.currentTimeMillis();
        SendConversationModel sendConversationModel = new SendConversationModel(binding.messageET.getText().toString(),
                currentUserId, String.valueOf(currentTimeStamp), "text", false, currentTimeStamp, selectedUserId);
        SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),
                currentUserId, String.valueOf(currentTimeStamp), selectedUserId, "text", false, currentTimeStamp);

        if (isFromNew.equals("admin")) {

            firebaseFirestore.collection("AdminChat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("AdminChat").document(conversationID).update("lastMessage", sendLastMessageModel);

        } else {

            firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chat").document(conversationID).update("lastMessage", sendLastMessageModel);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        firebaseRequests.deInit();

    }
}