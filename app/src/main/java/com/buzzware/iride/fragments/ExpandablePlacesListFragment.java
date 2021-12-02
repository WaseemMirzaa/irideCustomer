package com.buzzware.iride.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.iride.adapters.SavedLocationAdapter;
import com.buzzware.iride.response.autocomplete.AutoCompleteResponse;
import com.buzzware.iride.response.autocomplete.Prediction;
import com.buzzware.iride.response.placedetails.PlaceDetailResponse;
import com.buzzware.iride.OnPredictedEvent;
import com.buzzware.iride.OnTextChangedEvent;
import com.buzzware.iride.utils.AppConstants;
import com.buzzware.iride.databinding.FragmentExpandablePlacesListBinding;
import com.buzzware.iride.retrofit.Controller;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpandablePlacesListFragment extends BottomSheetDialogFragment implements SavedLocationAdapter.OnItemTappedListener {

    public static String TAG = ExpandablePlacesListFragment.class.getSimpleName();

    List<Prediction> locationModelList;

    SimpleLocation location;

    FragmentExpandablePlacesListBinding binding;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        binding = FragmentExpandablePlacesListBinding.inflate(inflater, container, false);

        setSheetBehaviour();

//        getData("Islamabad");

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    private void setSheetBehaviour() {

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout);

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        sheetBehavior.setDraggable(true);

        sheetBehavior.setHideable(true);

        sheetBehavior.setPeekHeight(new Float(pxFromDp(getActivity(),150)).intValue());

    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OnTextChangedEvent event) {

        getData(event.data);
    };

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void showBottomSheet(ShowBottomSheetMsg showBottomSheet) {

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout);

        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    };


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void hideBottomSheet(HideBottomSheet showBottomSheet) {

        BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    };


    @Subscribe

    private void getData(String data) {

        String url = "/maps/api/place/autocomplete/json?input=" + data + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        if(data == null) {

            url = "/maps/api/place/autocomplete/json?input=" + data + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;
        }

        Controller.getApi().getPlaces(url, "asdasd")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Gson gson = new Gson();

                        if (response.body() != null && response.isSuccessful()) {

                            AutoCompleteResponse autoCompleteResponse = gson.fromJson(response.body(), AutoCompleteResponse.class);

                            locationModelList = new ArrayList<>();

                            locationModelList.addAll(autoCompleteResponse.predictions);

                            setAdapter();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

    }


    private void setAdapter() {

        SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(getActivity(), locationModelList);

        binding.listPlacesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.listPlacesRV.setAdapter(savedLocationAdapter);

        savedLocationAdapter.setOnItemTappedListener(this);
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onLocationSelected(Prediction prediction) {

        getPlaceDetail(prediction);

    }

    private void getPlaceDetail(Prediction prediction) {

        String url = "/maps/api/place/details/json?place_id=" + prediction.place_id + "&key=" + AppConstants.GOOGLE_PLACES_API_KEY;

        Controller.getApi().getPlaces(url, "asdasd")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Gson gson = new Gson();

                        if (response.body() != null && response.isSuccessful()) {

                            PlaceDetailResponse placeDetail = gson.fromJson(response.body(), PlaceDetailResponse.class);

                            EventBus.getDefault().post(new OnPredictedEvent(placeDetail.result));

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

    }

    public static class ShowBottomSheetMsg {
    }

    public static class HideBottomSheet {
    }
}
