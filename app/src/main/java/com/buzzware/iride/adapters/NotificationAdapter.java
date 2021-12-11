package com.buzzware.iride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.iride.Firebase.FirebaseInstances;
import com.buzzware.iride.databinding.NotificationReadItemBinding;
import com.buzzware.iride.models.NotificationModel;
import com.buzzware.iride.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

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

        getProfilePic(notificationModel, viewHolder);

    }

    private void getProfilePic(NotificationModel notificationModel, ViewHolder viewHolder) {

        FirebaseInstances.
                usersCollection.document(notificationModel.getFromId())
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        User user = task.getResult().toObject(User.class);

                        if (user != null) {

                            Glide.with(mContext).load(user.image).apply(new RequestOptions().centerCrop())
                                    .into(viewHolder.binding.picCIV);
                        }

                    }

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
