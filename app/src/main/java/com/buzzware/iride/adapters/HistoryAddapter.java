package com.buzzware.iride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iride.models.HistoryModel;
import com.buzzware.iride.R;

import java.util.List;

public class HistoryAddapter extends RecyclerView.Adapter<HistoryAddapter.imageViewHolder>{
    public Context context;
    List<HistoryModel> historyModels;

    public HistoryAddapter(Context context, List<HistoryModel> historyModels) {
        this.context = context;
        this.historyModels = historyModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_lay, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final HistoryModel historyModel= historyModels.get(position);
    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public imageViewHolder(View itemView) {
            super(itemView);
            view= itemView;
        }
    }
}
