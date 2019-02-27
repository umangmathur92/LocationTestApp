package com.umangmathur.locationtestapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransitApiResponse {

    private List<Route> routeList = new ArrayList<>();

    public TransitApiResponse(JSONObject respJsonObj) {
        if (respJsonObj != null) {
            JSONArray routesJsonArr = respJsonObj.optJSONArray("routes");
            for (int i = 0; i < routesJsonArr.length(); i++) {
                JSONObject routeJsonObj = routesJsonArr.optJSONObject(i);
                Route route = new Route(routeJsonObj);
                routeList.add(route);
            }
        }
    }

    public List<Route> getRouteList() {
        return routeList;
    }
}
