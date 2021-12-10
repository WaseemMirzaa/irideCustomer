package com.buzzware.iride.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.buzzware.iride.R;
import com.buzzware.iride.adapters.NotificationAdapter;
import com.buzzware.iride.databinding.ActivityNotificationsBinding;
import com.buzzware.iride.models.MyRequests;
import com.buzzware.iride.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends BaseNavDrawer {

    ActivityNotificationsBinding binding;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    List<NotificationModel> notificationList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

        showRequest();

    }

    private void setListeners() {

        binding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

    }

    private void showRequest() {

        firebaseFirestore.collection("Notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    notificationList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        NotificationModel notification = document.toObject(NotificationModel.class);

                        notification.setId(document.getId());

                        if(notification.getToId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                            notificationList.add(notification);

                        }
                    }
                    setRecycler();
                }
            }
        });

    }

    private void setRecycler() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(Notifications.this);

        binding.notificationRV.setLayoutManager(layoutManager);

        NotificationAdapter normalBottleAdapter = new NotificationAdapter(Notifications.this, notificationList);

        binding.notificationRV.setAdapter(normalBottleAdapter);

    }

}