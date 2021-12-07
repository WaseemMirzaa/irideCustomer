package com.buzzware.iride.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {

    @FormUrlEncoded
    @POST()
    Call<String> getPlaces(@Url String url, @Field("email")String ema);
    // payment api
    @Headers("Accept: application/json")
    @POST("/bandy/api/getEphemeralKey")
    Call<String> getEphemeralKey(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/createclient")
    Call<String> createClient(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/paymentintent")
    Call<String> stripePaymentIntent(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/createaccount")
    Call<String> createAccount(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/linkaccount")
    Call<String> linkAccount(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/taxvaultfirststep")
    Call<String> createCustomer(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/taxvaultaccoutpayment")
    Call<String> accountPayment(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/cancelpayemt")
    Call<String> cancelPayment(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/checkaccount")
    Call<String> checkAccount(@Body RequestBody body);
}
