package com.buzzware.iride.screens;

import android.content.Intent;
import android.os.Bundle;
import android.service.quickaccesswallet.GetWalletCardsRequest;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.adapters.CardViewBindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.PaymentDialog;
import com.buzzware.iride.Stripe.PaymentResultCallback;
import com.buzzware.iride.adapters.PaymentMethodsAdapter;
import com.buzzware.iride.databinding.ActivityCardsBinding;
import com.buzzware.iride.models.User;
import com.buzzware.iride.models.myCardsList.CardListResponse;
import com.buzzware.iride.models.myCardsList.MyCard;
import com.buzzware.iride.response.paymentMethods.PaymentMethod;
import com.buzzware.iride.response.paymentMethods.PaymentMethodsResponse;
import com.buzzware.iride.retrofit.Controller;
import com.buzzware.iride.utils.AppConstants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buzzware.iride.retrofit.Controller.Base_Url_Payments;

public class MyCards extends BaseActivity {

    ActivityCardsBinding binding;

    String TAG = "MyCards";

    String customerId;

    Stripe stripe;

    PaymentDialog paymentDialog;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityCardsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

       stripe = new Stripe(this, AppConstants.stripeKey);

        binding.myCardsRV.setLayoutManager(new LinearLayoutManager(this));

        binding.btnAddPayment.setOnClickListener(view -> {

            if(paymentDialog != null && paymentDialog.isShowing())

                return;

            paymentDialog = new PaymentDialog(this, customerId,stripe);

            paymentDialog.show();

        });
        getMyProfile();
    }

    private void getMyProfile() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Users").document(getUserId());

        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            if (user.stripeCustid != null && !user.stripeCustid.isEmpty() && user.stripeCustid.length() > 3) {

                                customerId = user.stripeCustid;

                                getMyCards(user.stripeCustid);

                            } else {

                                createCard(user);

                            }
                        }

                    }

                });

    }

    private void createCard(User user) {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", user.firstName + user.lastName)
                .addFormDataPart("email", user.email)
                .addFormDataPart("cus_id", user.stripeCustid)

                .build();

        Controller.getApi(Base_Url_Payments).createCustomer(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        hideLoader();

                        if (response.body() != null) {

                            try {

                                JSONObject jsonObject = new JSONObject(response.body());

                                customerId = jsonObject.getString("cus_id");

                                FirebaseInstances.usersCollection.document(getUserId())
                                        .update("stripeCustid", customerId);

                                getMyCards(customerId);

                            } catch (Exception e) {

                                e.printStackTrace();

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(stripe.isSetupResult(requestCode, data)) {

            stripe.onSetupResult(requestCode, data, new ApiResultCallback<SetupIntentResult>() {
                @Override
                public void onSuccess(@NotNull SetupIntentResult setupIntentResult) {

                    getMyCards(customerId);

                }

                @Override
                public void onError(@NotNull Exception e) {

                    showErrorAlert(e.getLocalizedMessage());

                }
            });

        }
    }
    private void getMyCards(String cusId) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("cus_id", cusId);

        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());


        Controller.getApi(Controller.Base_Url_Payments_CF).getPaymentMethods(body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {

                            try {

                                PaymentMethodsResponse cardListResponse = new Gson().fromJson(response.body(), PaymentMethodsResponse.class);

                                showCardsList(cardListResponse.paymentMethods);

                            } catch (Exception e) {



                            }


                        }

                        Log.d(TAG, "onResponse: " + response.body());

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {


                    }
                });

    }



    private void showCardsList(ArrayList<PaymentMethod> list) {

        if(list == null)

            list = new ArrayList<>();

        binding.myCardsRV.setAdapter(new PaymentMethodsAdapter(MyCards.this, list, customerId));

    }
}
