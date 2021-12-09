package com.buzzware.iride.Stripe;

import android.util.Log;

import androidx.annotation.NonNull;

import com.buzzware.iride.screens.ConfirmPickupActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.StripeIntent;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class PaymentResultCallback  implements ApiResultCallback<PaymentIntentResult> {
    @NonNull
    private final WeakReference<ConfirmPickupActivity> activityRef;

    public PaymentResultCallback(@NonNull ConfirmPickupActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    public void onSuccess(@NonNull PaymentIntentResult result) {
        final ConfirmPickupActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }

        PaymentIntent paymentIntent = result.getIntent();
        PaymentIntent.Status status = paymentIntent.getStatus();
        Log.d("Status", "onSuccess: Status" + status);
        if (status == StripeIntent.Status.RequiresCapture) {
            activity.showSuccessMessage();
        } else if (status == PaymentIntent.Status.Succeeded) {
            // Payment completed successfully
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            activity.showSuccessMessage();
        } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
            // Payment failed – allow retrying using a different payment method
            activity.showErrorAlert(
                    "Payment failed " +
                            Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
            );
            Log.d("Test", "onSuccess: " + paymentIntent.getLastPaymentError());
        }
    }


    @Override
    public void onError(@NonNull Exception e) {
        final ConfirmPickupActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }
        Log.d("Test", "Errpr: " + e.toString());
        activity.showErrorAlert("Error " + e.toString());
    }


}
