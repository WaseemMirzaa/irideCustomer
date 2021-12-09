package com.buzzware.iride.screens;

import android.os.Bundle;

import android.widget.Toast;

import com.buzzware.iride.databinding.FragmentCustomerServiceBinding;
import com.buzzware.iride.models.MyRequests;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class CustomerService extends BaseNavDrawer {

    FragmentCustomerServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = FragmentCustomerServiceBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {

        binding.btnContinue.setOnClickListener(v -> validateAndCreateRequest());

        binding.drawerIcon.setOnClickListener(v -> openCloseDrawer());

    }

    private void validateAndCreateRequest() {

        if (validate()) {

            hideKeyboard();

            MyRequests myRequests = new MyRequests();

            myRequests.email = binding.emailET.getText().toString();
            myRequests.message = binding.messageET.getText().toString();
            myRequests.subject = binding.subjectET.getText().toString();
            myRequests.name = binding.nameET.getText().toString();
            myRequests.timeStamp = new Date().getTime();
            myRequests.userId = getUserId();

            showLoader();

            FirebaseFirestore.getInstance().collection("MyRequests")
                    .document()
                    .set(myRequests)
                    .addOnCompleteListener(task -> {

                        hideLoader();

                        Toast.makeText(CustomerService.this, "Request Submitted", Toast.LENGTH_SHORT).show();

                        finish();

                    });

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