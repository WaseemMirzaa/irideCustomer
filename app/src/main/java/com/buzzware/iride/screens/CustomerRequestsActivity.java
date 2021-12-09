package com.buzzware.iride.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.iride.R;
import com.buzzware.iride.adapters.RequestAdapter;
import com.buzzware.iride.databinding.ActivityCustomerRequestsBinding;
import com.buzzware.iride.fragments.BaseFragment;
import com.buzzware.iride.interfaces.RequestCallback;
import com.buzzware.iride.models.MyRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.util.ArrayList;
import java.util.List;

public class CustomerRequestsActivity extends BaseNavDrawer implements RequestCallback {

    ActivityCustomerRequestsBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    List<MyRequests> myRequests = new ArrayList<>();

    String adminId="5p4owdD4RkQsRdlZZ8nuQR6u78F2";

    String adminName="AdminUser";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityCustomerRequestsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListener();

        showRequest();
    }


    private void showRequest() {

        firebaseFirestore.collection("MyRequests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    myRequests.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        MyRequests requests = document.toObject(MyRequests.class);

                        requests.id = document.getId();

                        if(requests.userId.equals(getUserId())){

                            myRequests.add(requests);

                        }

                    }

                    setRecycler();
                }
            }
        });

    }

    private void setRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        binding.requestRV.setLayoutManager(layoutManager);

        RequestAdapter requestAdapter = new RequestAdapter(CustomerRequestsActivity.this, myRequests,this);

        binding.requestRV.setAdapter(requestAdapter);

    }


    private void setListener() {

        binding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

        binding.createNewRequestBtn.setOnClickListener(v -> startActivity(new Intent(CustomerRequestsActivity.this, CreateNewRequestActivity.class)));

    }

    @Override
    public void onItemClick(String requestId, String ConversationId) {
        Intent intent = new Intent(CustomerRequestsActivity.this, MessagesActivity.class);

        intent.putExtra("conversationID",ConversationId);
        intent.putExtra("selectedUserID",adminId );
        intent.putExtra("selectedUserName", adminName);
        intent.putExtra("checkFrom", "false");

        startActivity(intent);
    }

}