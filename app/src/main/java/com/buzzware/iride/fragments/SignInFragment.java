package com.buzzware.iride.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.FragmentSignInBinding;
import com.buzzware.iride.models.User;
import com.buzzware.iride.screens.BookARideActivity;
import com.buzzware.iride.utils.AlertUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class SignInFragment extends BaseFragment {

    FragmentSignInBinding mBinding;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_sign_in,
                container,
                false);

        FirebaseApp.initializeApp(getActivity());

        init();

        return mBinding.getRoot();
    }

    void showForgetPassword() {

        AlertUtils.showSingleInputDialog(getActivity(),
                "Reset Password",
                "Email",
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
                "Send",
                "Cancel",
                new AlertUtils.SingleInputDialogListener() {
                    @Override
                    public void positiveCallback(String input) {

                        if (input.toString().isEmpty()) {

                            Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(input)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(getActivity(), "Sent Successfully", Toast.LENGTH_SHORT).show();

                                        } else {

                                            if (task.getException() != null)
                                                showErrorAlert(task.getException().getLocalizedMessage());

                                        }
                                    }
                                });
                    }

                    @Override
                    public void negativeCallback() {

                        // ...

                    }
                });

    }
    private void init() {

        mBinding.tvForgot.setOnClickListener(v -> showForgetPassword());
        mBinding.btnLogin.setOnClickListener(v -> signIn());
    }

    private void signIn() {

        if (validate()) {

            String email = mBinding.emailET.getText().toString();
            String password = mBinding.passwordET.getText().toString();

            showLoader();

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this::onComplete);
        }
    }

    private boolean validate() {

        if (mBinding.emailET.getText().toString().isEmpty()) {

            showErrorAlert("Email Required");

            return false;
        }

        if (mBinding.passwordET.getText().toString().isEmpty()) {

            showErrorAlert("Password Required");

            return false;
        }

        return true;
    }

    private void onComplete(Task<AuthResult> task) {

        hideLoader();

        if (task.isSuccessful()) {

            getCurrentUserData();

        } else {

            if (task.getException() == null || task.getException().getLocalizedMessage() == null)

                return;

            showErrorAlert(task.getException().getMessage());
        }
    }

    private void getCurrentUserData() {

        DocumentReference users = FirebaseFirestore.getInstance().collection("Users").document(getUserId());

        users.addSnapshotListener((value, error) -> {

            if (value != null) {

                User user = value.toObject(User.class);

                if (user == null || user.userRole == null)
                    return;

                if (user.userRole.equalsIgnoreCase("user")) {

                    startActivity(new Intent(getActivity(), BookARideActivity.class));

                } else {

                    FirebaseAuth.getInstance().signOut();

                    showErrorAlert("Invalid Email. You have used this email as a driver. Can't use same email in customer app.");

                }
            }


        });

    }
}