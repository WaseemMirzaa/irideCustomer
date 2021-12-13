package com.buzzware.iride.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.iride.databinding.ItemPaymentBinding;
import com.buzzware.iride.models.ScheduleModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.PaymentHolder> {

    Context context;

    List<ScheduleModel> list;

    public PaymentsAdapter(Context context, List<ScheduleModel> list) {

        this.context = context;

        this.list = list;

    }

    @Override
    public @NotNull PaymentHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemPaymentBinding binding = ItemPaymentBinding.inflate(LayoutInflater.from(context), parent, false);

        return new PaymentHolder(binding);
    }

    @Override
    public void onBindViewHolder(final PaymentHolder holder, final int position) {

        ScheduleModel ride = list.get(position);

        holder.bind(ride);

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class PaymentHolder extends RecyclerView.ViewHolder {

        public ItemPaymentBinding binding;

        public PaymentHolder(ItemPaymentBinding binding) {

            super(binding.getRoot());

            this.binding = binding;

        }

        @SuppressLint("SetTextI18n")
        public void bind(ScheduleModel ride) {

            if (ride.status != null)

                binding.statusTV.setText(ride.status.toUpperCase());

            if (ride.price != null)

                binding.priceTV.setText("Price $" + String.format("%.2f", Double.parseDouble(ride.price)));

            String pickupAddress = getPickUpAddress(ride);
            String destinationAddress = getDestinationAddress(ride);

            if (pickupAddress != null)

                binding.pickUpAddressTV.setText(pickupAddress);

            if (destinationAddress != null)

                binding.destinationAddressTV.setText(destinationAddress);

            if (ride.tripDetail.destinations.size() > 1) {

                String dropOffAddress = ride.tripDetail.destinations.get(0).address;
                destinationAddress = ride.tripDetail.destinations.get(1).address;

                binding.destinationAddressTV.setText(destinationAddress);

                binding.dropOffTv.setText(dropOffAddress);

                binding.dropOffVw.setVisibility(View.VISIBLE);
                binding.dropOffLL.setVisibility(View.VISIBLE);

            } else {

                binding.dropOffVw.setVisibility(View.GONE);

                binding.dropOffLL.setVisibility(View.GONE);

            }

            if (ride.scheduledDate != null && ride.scheduledTime != null)

                binding.timeTV.setText("Date: " + ride.scheduledDate + "\nTime: " + ride.scheduledTime);

            else if (getDateTime(ride.bookingDate) != null)

                binding.timeTV.setText(getDateTime(ride.bookingDate));

        }

    }

    private String getDestinationAddress(ScheduleModel ride) {

        if (ride.tripDetail.destinations.get(0) != null && ride.tripDetail.destinations.get(0).address != null)

            return ride.tripDetail.destinations.get(0).address;

        return "";
    }

    private String getDateTime(long bookingDate) {

        Date date = new Date(bookingDate);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat tf = new SimpleDateFormat(" HH:MM");

        return  "Date: "+df.format(date)+"\nTime: "+tf.format(date);

    }

    private String getPickUpAddress(ScheduleModel ride) {

        if (ride.tripDetail.pickUp != null && ride.tripDetail.pickUp.address != null)

            return ride.tripDetail.pickUp.address;

        return "";
    }

}
