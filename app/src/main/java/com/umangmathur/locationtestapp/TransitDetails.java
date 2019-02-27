package com.umangmathur.locationtestapp;

import org.json.JSONObject;

public class TransitDetails {

    private String shortName = "";
    private int numStops;
    private String name = "";
    private String vehicleType = "";
    private String vehicleName = "";

    public TransitDetails() {
    }

    public TransitDetails(JSONObject transitDetailsJsonObj) {
        if (transitDetailsJsonObj != null) {
            this.numStops = transitDetailsJsonObj.optInt("num_stops");
            JSONObject lineJsonObj = transitDetailsJsonObj.optJSONObject("line");
            this.name = lineJsonObj.optString("name");
            this.shortName = lineJsonObj.optString("short_name");
            JSONObject vehicleJsonObj = lineJsonObj.optJSONObject("vehicle");
            this.vehicleName = vehicleJsonObj.optString("name");
            this.vehicleType = vehicleJsonObj.optString("type");
        }
    }

    public String getShortName() {
        return shortName;
    }

    public int getNumStops() {
        return numStops;
    }

    public String getName() {
        return name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleName() {
        return vehicleName;
    }

}
