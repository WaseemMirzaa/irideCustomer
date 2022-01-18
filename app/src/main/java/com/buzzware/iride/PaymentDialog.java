package com.buzzware.iride;

import android.app.Dialog;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.buzzware.iride.retrofit.Controller;
import com.buzzware.iride.screens.BaseActivity;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardFormView;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buzzware.iride.retrofit.Controller.Base_Url_Payments;

public class PaymentDialog extends Dialog {

    Stripe stripe;
    BaseActivity c;
    private CardView cv;
    Boolean valid;
    private CardFormView cardFormView;
    private RelativeLayout btnConfrim;
    String customerId;

    public PaymentDialog(@NonNull BaseActivity context, String customerId, Stripe stripe) {

        super(context, R.style.Theme_Transparent);

        this.stripe = stripe;
;

        this.customerId = customerId;

        c = context;

        setContentView(R.layout.payment_dialog);

        initView();

        setCancelable(true);

        cardFormView.setCardValidCallback((b, set) -> valid = b);

        btnConfrim.setOnClickListener(v -> {

            if (valid) {

                createCard();

            } else {

                c.showErrorAlert("Incorrect Card Details");

            }

        });

    }

    private void createCard() {

        c.showLoader();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("cus_id", customerId)
                .build();

        Controller.getApi(Base_Url_Payments).createCustomer(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        c.hideLoader();

                        if (response.body() != null) {

                            try {

                                JSONObject jsonObject = new JSONObject(response.body());

                                String clientSecret = jsonObject.getString("key");

                                PaymentMethodCreateParams params1 = PaymentMethodCreateParams.createCard(cardFormView.getCardParams());

                                ConfirmSetupIntentParams confirmSetupIntentParams = ConfirmSetupIntentParams.create(params1,clientSecret);

                                stripe.confirmSetupIntent(c, confirmSetupIntentParams);

                                dismiss();

                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });



//        stripe.confirmSe
//        CardParams card = cardFormView.getCardParams();

//        SourceParams cardSourceParams = SourceParams.createCardParams(card);

//        Stripe.apiKey = "sk_test_26PHem9AhJZvU623DfE1x4sd";
//
//        SetupIntentCreateParams params =
//                SetupIntentCreateParams
//                        .builder()
//                        .setUsage(SetupIntentCreateParams.Usage.ON_SESSION)
//                        .build()
//                        ;


//        PaymentMethodCreateParams params =
//        ConfirmSetupIntentParams confirmSetupIntentParams = ConfirmSetupIntentParams.create()
//        stripe.confirmSetupIntent(c,cardSourceParams);
//        SetupIntent setupIntent = SetupIntent.create(params);
//        stripe.createSource(
//                cardSourceParams,
//                new ApiResultCallback<Source>() {
//                    @Override
//                    public void onSuccess(@NonNull Source source) {
//
//                        c.hideLoader();
//
//                        Toast.makeText(c, "Successfully Created", Toast.LENGTH_SHORT).show();
////source.
//                        listener.onCardCreated();
//
//                        dismiss();
//                        // Store the source somewhere, use it, etc
//                    }
//
//                    @Override
//                    public void onError(@NonNull Exception error) {
//
//                        c.hideLoader();
//
//                        c.showErrorAlert(error.getLocalizedMessage());
//
//                    }
//                });

//        CustomerCre customerParams = CustomerCreateParams.builder().build();
//        Customer customer = Customer.create(customerParams);
//
//        EphemeralKeyCreateParams ephemeralKeyParams =
//                EphemeralKeyCreateParams.builder()
//                        .setCustomer(customer.getId())
//                        .build();
//
//        RequestOptions ephemeralKeyOptions =
//                RequestOptions.RequestOptionsBuilder())
//      .setStripeVersionOverride("2020-08-27")
//                .build();
//
//        EphemeralKey ephemeralKey = EphemeralKey.create(
//                ephemeralKeyParams,
//                ephemeralKeyOptions);
//
//        SetupIntentCreateParams setupIntentParams =
//                SetupIntentCreateParams.builder()
//                        .setCustomer(customer.getId())
//                        .build();
//        SetupIntent setupIntent = SetupIntent.create(setupIntentParams);

    }

    private void initView() {
        cv = (CardView) findViewById(R.id.cv);
        cardFormView = (CardFormView) findViewById(R.id.card_form_view);
        btnConfrim = (RelativeLayout) findViewById(R.id.btnConfrim);
    }

}
