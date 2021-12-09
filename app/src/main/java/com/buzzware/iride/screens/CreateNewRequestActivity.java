package com.buzzware.iride.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.buzzware.iride.databinding.ActivityCreateNewRequestBinding;
import com.buzzware.iride.models.MyRequests;
import com.buzzware.iride.models.SendConversationModel;
import com.buzzware.iride.models.SendLastMessageModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class CreateNewRequestActivity extends BaseActivity {

    ActivityCreateNewRequestBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String adminId="5p4owdD4RkQsRdlZZ8nuQR6u78F2";

    String adminName="AdminUser";

    String conversationID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityCreateNewRequestBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.btnContinue.setOnClickListener(v -> validateAndSendMessage());

    }

    private void createRequest(String cId) {

            hideKeyboard();

            MyRequests myRequests = new MyRequests();

            myRequests.email = binding.emailET.getText().toString();
            myRequests.message = binding.messageET.getText().toString();
            myRequests.subject = binding.subjectET.getText().toString();
            myRequests.name = binding.nameET.getText().toString();
            myRequests.timeStamp = new Date().getTime();
            myRequests.conversationId=cId;
            myRequests.userId = getUserId();

            showLoader();

            FirebaseFirestore.getInstance().collection("MyRequests")
                    .document()
                    .set(myRequests)
                    .addOnCompleteListener(task -> {

                        hideLoader();

                        Toast.makeText(CreateNewRequestActivity.this, "Request Submitted", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);

                        intent.putExtra("conversationID",cId);
                        intent.putExtra("selectedUserID",adminId );
                        intent.putExtra("selectedUserName", adminName);
                        intent.putExtra("checkFrom", "false");

                        startActivity(intent);

                        finish();

                    });

    }

    private void validateAndSendMessage() {
        if (validate()) {
            long currentTimeStamp = System.currentTimeMillis();

            String message="Name : "+binding.nameET.getText().toString()+"\nSubject : "+binding.subjectET.getText().toString()
                    +"\nEmail : "+binding.emailET.getText().toString()+"\nMessage : "+binding.messageET.getText().toString();

            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(binding.messageET.getText().toString(),

                    getUserId(), String.valueOf(currentTimeStamp), adminId, "text", false, (int) currentTimeStamp);

            HashMap<String, Boolean> participents = new HashMap<>();

            participents.put(getUserId(), true);

            participents.put(adminId, true);

            SendConversationModel sendConversationModel = new SendConversationModel(message,
                    getUserId(), String.valueOf(currentTimeStamp), "text", false, currentTimeStamp);

            HashMap<String, Object> lasthashMap = new HashMap<>();

            lasthashMap.put("lastMessage", sendLastMessageModel);

            lasthashMap.put("participants", participents);

            conversationID = UUID.randomUUID().toString();

            firebaseFirestore.collection("Chat").document(conversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            firebaseFirestore.collection("Chat").document(conversationID).set(lasthashMap);

            createRequest(conversationID);

        }

    }


    private boolean validate() {

        if (binding.nameET.getText().toString().isEmpty()) {

            showErrorAlert("Name Required");

            return false;
        }


        if (binding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }


        if (binding.subjectET.getText().toString().isEmpty()) {

            showErrorAlert("Subject Required");

            return false;
        }


        if (binding.messageET.getText().toString().isEmpty()) {

            showErrorAlert("Message Required");

            return false;
        }

        return true;

    }
}