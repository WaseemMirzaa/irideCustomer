package com.buzzware.iride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.R;
import com.buzzware.iride.databinding.NotificationReadItemBinding;
import com.buzzware.iride.models.NotificationModel;
import com.buzzware.iride.screens.NotificationDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> list;

    private Context mContext;


    public NotificationAdapter(Context mContext, List<NotificationModel> list) {

        this.list = list;

        this.mContext = mContext;

    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(NotificationReadItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        NotificationModel notificationModel=list.get(i);

        viewHolder.binding.titleTV.setText(notificationModel.getTitle());

        viewHolder.binding.messageTV.setText(notificationModel.getMessage());

        Glide.with(mContext).load(R.drawable.logo).apply(new RequestOptions().centerCrop())
                .into(viewHolder.binding.picCIV);

        viewHolder.binding.getRoot().setOnClickListener(v -> {

            notificationModel.isRead.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);

            FirebaseFirestore.getInstance().collection("Notification")
                    .document(notificationModel.getId())
                    .set(notificationModel);

            NotificationDetail.startNotificationDetail(mContext, notificationModel.getTitle(), notificationModel.getMessage());

        });
    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NotificationReadItemBinding binding;


        public ViewHolder(@NonNull NotificationReadItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
