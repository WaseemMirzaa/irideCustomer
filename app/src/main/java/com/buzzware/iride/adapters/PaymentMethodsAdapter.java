package com.buzzware.iride.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.AlreadySavedCardItemBinding;
import com.buzzware.iride.response.paymentMethods.PaymentMethod;
import com.buzzware.iride.retrofit.Controller;
import com.buzzware.iride.screens.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buzzware.iride.retrofit.Controller.Base_Url_Payments;
import static com.buzzware.iride.retrofit.Controller.Base_Url_Payments_CF;
import static com.buzzware.iride.retrofit.Controller.Base_Url_Stripe;

public class PaymentMethodsAdapter extends RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder> {

    private List<PaymentMethod> list;

    private BaseActivity mContext;

    public String key;

    String customerId;

    public PaymentMethodsAdapter(BaseActivity mContext, ArrayList<PaymentMethod> list, String customerId) {

        this.customerId = customerId;

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public PaymentMethodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new PaymentMethodsAdapter.ViewHolder(AlreadySavedCardItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentMethodsAdapter.ViewHolder viewHolder, final int i) {

        PaymentMethod method = list.get(i);

        if (method.last4 != null)
            viewHolder.binding.tv.setText(method.last4);

        if(method.brand.equalsIgnoreCase("American Express"))

            Glide.with(mContext).load(R.drawable.american_express).apply(new RequestOptions().centerCrop())
                    .into(viewHolder.binding.card);

        else if(method.brand.equalsIgnoreCase("Diners Club"))

            Glide.with(mContext).load(R.drawable.master_card).apply(new RequestOptions().centerCrop())
                    .into(viewHolder.binding.card);

        else if(method.brand.equalsIgnoreCase("American Express"))

            Glide.with(mContext).load(R.drawable.american_express).apply(new RequestOptions().centerCrop())
                    .into(viewHolder.binding.card);

        else if(method.brand.equalsIgnoreCase("American Express"))

            Glide.with(mContext).load(R.drawable.american_express).apply(new RequestOptions().centerCrop())
                    .into(viewHolder.binding.card);

        else if(method.brand.equalsIgnoreCase("American Express"))

            Glide.with(mContext).load(R.drawable.american_express).apply(new RequestOptions().centerCrop())
                    .into(viewHolder.binding.card);

        viewHolder.binding.rightArrow.setOnClickListener(v -> {

            getKey(method, i);

        });

    }

    void getKey(PaymentMethod method, int i) {

        mContext.showLoader();


        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("pm_id", method.id);

        } catch (Exception e) {

        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());


        Controller.getApi(Base_Url_Payments_CF).deAttachPaymentMethod(body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        mContext.hideLoader();
//                        retriveCards

                        if(response.body() != null && response.body().contains("1")) {

                            list.remove(i);

                            notifyItemRemoved(i);

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mContext.hideLoader();
                    }
                });
    }



    private void deleteCard(PaymentMethod method) {

        mContext.showLoader();

        Controller.getApi(Base_Url_Stripe)
                .deleteCard(customerId+"/sources/"+method.id,"Bearer "+key)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        mContext.showLoader();

//                        Log.d(TAG, "onResponse: ");

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        mContext.hideLoader();
                    }
                });

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AlreadySavedCardItemBinding binding;

        public ViewHolder(@NonNull AlreadySavedCardItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
