package com.umangmathur.locationtestapp;

import org.json.JSONObject;

public class Step {

    private String travelMode = "";
    private String polyLinePoints = "";
    private TransitDetails transitDetails = new TransitDetails();

    public Step(JSONObject stepJsonObject) {
        if (stepJsonObject!=null) {
            String travelMode = stepJsonObject.optString("travel_mode");
            String polyLinePoints = stepJsonObject.optJSONObject("polyline").optString("points");
            JSONObject transitDetailsJsonObj = stepJsonObject.optJSONObject("transit_details");
            this.transitDetails = new TransitDetails(transitDetailsJsonObj);
            this.travelMode = travelMode;
            this.polyLinePoints = polyLinePoints;
        }
    }

    public String getTravelMode() {
        return travelMode;
    }

    public String getPolyLinePoints() {
        return polyLinePoints;
    }

    public TransitDetails getTransitDetails() {
        return transitDetails;
    }
}
