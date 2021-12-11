package com.buzzware.iride.models.settings;

import com.google.gson.annotations.SerializedName;

public class SettingsObj {

    @SerializedName("Prices")
    public Prices prices;

    public DriverShare driverShare;
}