package com.umangmathur.locationtestapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class Route {

    private String overViewPolyLinePoints = "";
    private List<Leg> legsList = new ArrayList<>();

    public Route(JSONObject routeJsonObj) {
        if (routeJsonObj != null) {
            this.overViewPolyLinePoints = routeJsonObj.optJSONObject("overview_polyline").optString("points");
            JSONArray legsJsonArr = routeJsonObj.optJSONArray("legs");
            for (int i = 0; i < legsJsonArr.length(); i++) {
                JSONObject legJsonObj = legsJsonArr.optJSONObject(i);
                Leg leg = new Leg(legJsonObj);
                this.legsList.add(leg);
            }
        }
    }

    public String getOverViewPolyLinePoints() {
        return overViewPolyLinePoints;
    }

    public List<Leg> getLegsList() {
        return legsList;
    }
}
