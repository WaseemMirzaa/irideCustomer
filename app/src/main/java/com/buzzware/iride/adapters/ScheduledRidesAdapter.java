package com.buzzware.iride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.buzzware.iride.R;
import com.buzzware.iride.models.ScheduleModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ScheduledRidesAdapter extends RecyclerView.Adapter<ScheduledRidesAdapter.UpcomingRidesHolder> {

    Context context;

    List<ScheduleModel> historyModels;

    RideType rideType;

    public ScheduledRidesAdapter(Context context, List<ScheduleModel> historyModels) {

        this.context = context;

        this.historyModels = historyModels;

    }

    @Override
    public UpcomingRidesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_schedule,
                        parent,
                        false
                );

        return new UpcomingRidesHolder(view);
    }

    @Override
    public void onBindViewHolder(final UpcomingRidesHolder holder, final int position) {

        ScheduleModel ride = historyModels.get(position);

        holder.pickUpAddressTV.setText(getPickUpAddress(ride));

        holder.destinationAddressTV.setText(getDestinationAddress(ride));

        if (ride.tripDetail.destinations.size() > 1) {

            holder.destinationAddressTV.setText(ride.tripDetail.destinations.get(1).address);

            holder.dropOffTv.setText(ride.tripDetail.destinations.get(0).address);

            holder.dropOffVw.setVisibility(View.VISIBLE);
            holder.dropOffLL.setVisibility(View.VISIBLE);

        } else {

            holder.dropOffVw.setVisibility(View.GONE);

            holder.dropOffLL.setVisibility(View.GONE);

        }

        holder.timeTV.setText("Date: " + getDate(ride.bookingDate) + "\nTime: " + getTime(ride.scheduleTimeStamp));

//        holder.acceptNowBt.setVisibility(View.GONE);

    }

    private String getTime(long bookingDate) {

        Date date = new Date(bookingDate);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:MM");

        return formatter.format(date);

    }


    private String getDate(long bookingDate) {

        Date date = new Date(bookingDate);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        return formatter.format(date);

    }

    private String getPickUpAddress(ScheduleModel ride) {

        if (ride.tripDetail.pickUp != null && ride.tripDetail.pickUp.address != null)

            return ride.tripDetail.pickUp.address;

        return "";
    }

    private String getDestinationAddress(ScheduleModel ride) {

        if (ride.tripDetail.destinations.get(0) != null && ride.tripDetail.destinations.get(0).address != null)

            return ride.tripDetail.destinations.get(0).address;

        return "";
    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class UpcomingRidesHolder extends RecyclerView.ViewHolder {

        public TextView pickUpAddressTV;

        public TextView destinationAddressTV;

        public TextView timeTV;

//        public TextView acceptTV;

        public RelativeLayout acceptNowBt;
        public RelativeLayout relativeLayout;
        public View dropOffVw;
        public RelativeLayout dropOffLL;
        public TextView dropOffTv;

        public UpcomingRidesHolder(View itemView) {
            super(itemView);

            initView(itemView);
        }

        private void initView(View view) {

            pickUpAddressTV = itemView.findViewById(R.id.pickUpAddressTV);

            timeTV = itemView.findViewById(R.id.timeTV);

            destinationAddressTV = itemView.findViewById(R.id.destinationAddressTV);

            acceptNowBt = itemView.findViewById(R.id.acceptNowBt);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            dropOffVw = (View) itemView.findViewById(R.id.dropOffVw);
            dropOffLL = (RelativeLayout) itemView.findViewById(R.id.dropOffLL);
            dropOffTv = (TextView) itemView.findViewById(R.id.dropOffTv);
        }
    }

}
