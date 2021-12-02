package com.buzzware.iride.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.R;
import com.buzzware.iride.models.User;
import com.buzzware.iride.screens.BaseNavDrawer;
import com.buzzware.iride.screens.EditProfileActivity;
import com.buzzware.iride.screens.Notifications;
import com.buzzware.iride.screens.Settings;
import com.buzzware.iride.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

        binding.editIcon.setOnClickListener(v->{

            startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));

        });

    }

    private void getCurrentUserData() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getUid() != null) {

            DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection("Users").document(user.getUid());
            documentReferenceBuisnessUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    User user = value.toObject(User.class);

                    setUserData(user);

                }
            });

        }

    }

    private void setUserData(User user) {

        if (user.image != null && !user.image.isEmpty()) {
            Glide.with(getApplicationContext()).load(user.image).apply(new RequestOptions().centerCrop()).into(binding.userImageIV);
        }
        binding.userNameTV.setText(user.firstName + " " + user.lastName);
        binding.userAddressTV.setText(user.address);
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
            startActivity(new Intent(getApplicationContext(), Settings.class));
        } else if (v == binding.btnChat) {
            startActivity(new Intent(Profile.this, Chat.class));
        }
    }
}