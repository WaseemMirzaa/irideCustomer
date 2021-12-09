package com.buzzware.iride.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.databinding.FragmentProfileBinding;
import com.buzzware.iride.models.User;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.buzzware.iride.screens.EditProfileActivity;
import com.buzzware.iride.screens.Notifications;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends BaseNavDrawer implements View.OnClickListener {

    FragmentProfileBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Init();

        getCurrentUserData();

        setListener();

    }

    private void setListener() {

        binding.editIcon.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), EditProfileActivity.class)));

    }

    private void getCurrentUserData() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            DocumentReference reference = firebaseFirestore.collection("Users").document(user.getUid());

            reference.addSnapshotListener((value, error) -> {

                if (value != null) {

                    User user1 = value.toObject(User.class);

                    if (user1 != null) {

                        setUserData(user1);

                    }

                }


            });

        }

    }

    private void setUserData(User user) {

        if (user.image != null && !user.image.isEmpty()) {

            Glide.with(getApplicationContext()).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userImageIV);

        }

        binding.userNameTV.setText(user.firstName + " " + user.lastName);

        binding.userAddressTV.setText(user.homeAddress);

        binding.userPhoneNumberTV.setText(user.phoneNumber);

    }


    private void Init() {

        binding.drawerIcon.setOnClickListener(v -> OpenCloseDrawer());

        binding.btnNotifications.setOnClickListener(this);

        binding.btnSettings.setOnClickListener(this);

        binding.btnChat.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        super.onClick(v);

        if (v == binding.btnNotifications) {

            startActivity(new Intent(getApplicationContext(), Notifications.class));

        } else if (v == binding.btnSettings) {

            startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));

        } else if (v == binding.btnChat) {

            startActivity(new Intent(Profile.this, Chat.class));

        }
    }
}