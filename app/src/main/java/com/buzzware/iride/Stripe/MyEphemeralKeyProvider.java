package com.buzzware.iride.Stripe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.buzzware.iride.retrofit.Controller;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEphemeralKeyProvider implements EphemeralKeyProvider {
    public static String cusId="";
    @Override
    public void createEphemeralKey(
            @NonNull @Size(min = 4) String apiVersion,
            @NonNull final EphemeralKeyUpdateListener keyUpdateListener) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("api_version", apiVersion);
//            jsonObject.put("cus_id", cusId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_version", apiVersion)
                .addFormDataPart("cus_id", cusId)
                .build();
        Controller.getApi(Controller.Base_Url_Payments).getEphemeralKey(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response.body());
                            JSONObject key = jsonObject1.getJSONObject("key");
                            String id = key.getString("id");
                            if (response.body() != null)
                                keyUpdateListener.onKeyUpdate(key.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("epherialkey",e.getMessage());
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        keyUpdateListener.onKeyUpdateFailure(10, "Failed to get Empheral key");
                    }
                });
    }
}
