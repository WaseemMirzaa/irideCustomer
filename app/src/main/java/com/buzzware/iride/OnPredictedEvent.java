package com.buzzware.iride;

import com.buzzware.iride.response.placedetails.PlaceDetail;

public class OnPredictedEvent {

    public PlaceDetail place;

    public OnPredictedEvent(PlaceDetail result) {
        place = result;
    }
}
