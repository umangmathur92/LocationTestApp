package com.umangmathur.locationtestapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class Leg {

    private List<Step> stepList = new ArrayList<>();

    public Leg(JSONObject legJsonObj) {
        if (legJsonObj != null) {
            JSONArray stepsJsonArr = legJsonObj.optJSONArray("steps");
            for (int j = 0; j < stepsJsonArr.length(); j++) {
                JSONObject stepJsonObject = stepsJsonArr.optJSONObject(j);
                Step step = new Step(stepJsonObject);
                this.stepList.add(step);
            }
        }
    }

    public List<Step> getStepList() {
        return stepList;
    }

}
