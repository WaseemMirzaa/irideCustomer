package com.buzzware.iride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iride.response.autocomplete.Prediction;
import com.buzzware.iride.R;

import java.util.List;

public class SavedLocationAdapter extends RecyclerView.Adapter<SavedLocationAdapter.imageViewHolder> {
    public Context context;
    List<Prediction> savedLocationModels;

    OnItemTappedListener onItemTappedListener;

    public SavedLocationAdapter(Context context, List<Prediction> savedLocationModels) {

        this.context = context;

        this.savedLocationModels = savedLocationModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_locations_item, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {

        Prediction prediction = savedLocationModels.get(position);

        holder.titleTV.setText(prediction.structured_formatting.main_text);

        holder.descTV.setText(prediction.description);

        holder.view.setOnClickListener(v -> onItemTappedListener.onLocationSelected(prediction));
    }

    @Override
    public int getItemCount() {
        return savedLocationModels.size();
    }

    public void setOnItemTappedListener(OnItemTappedListener onItemTappedListener) {
        this.onItemTappedListener = onItemTappedListener;
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;
        public TextView titleTV;
        public TextView descTV;

        public imageViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            initView();
        }

        private void initView() {
            titleTV = (TextView) view.findViewById(R.id.titleTV);
            descTV = (TextView) view.findViewById(R.id.descTV);
        }
    }

    public interface OnItemTappedListener {
        void onLocationSelected(Prediction prediction);
    }
}
