package com.umangmathur.locationtestapp;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.umangmathur.locationtestapp.RoutesAdapter.ClickHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "UMG";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private FusedLocationProviderClient fusedLocationClient;
    private int locationRequestCode = 1;
    private String[] permissionsArray = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
    private ImageButton imgBtnLocation, imgBtnSearch;
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment1, placeAutocompleteFragment2;
    private Geocoder geocoder;
    private LatLng sourceLatLong, destLatLong;
    private String placeIdSource = "", placeIdDest = "";
    private GoogleMap googleMap;
    private RequestQueue queue;
    private List<Step> stepsToBeDisplayedInPopUp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgBtnLocation = findViewById(R.id.imgBtnLocation);
        imgBtnSearch = findViewById(R.id.imgBtnSearch);
        placeAutocompleteFragment1 = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);
        placeAutocompleteFragment2 = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35, -100), 3));
                MainActivity.this.googleMap = googleMap;
            }
        });
        ImageView searchIcon = (ImageView) ((LinearLayout) placeAutocompleteFragment1.getView()).getChildAt(0);
        searchIcon.setImageDrawable(null);
        ImageView searchIcon2 = (ImageView) ((LinearLayout) placeAutocompleteFragment2.getView()).getChildAt(0);
        searchIcon2.setImageDrawable(null);
        imgBtnLocation.setOnClickListener(this);
        imgBtnSearch.setOnClickListener(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsArray, locationRequestCode);
        } else {
            //getUserLocation();
        }
        placeAutocompleteFragment1.setOnPlaceSelectedListener(getSourcePlaceSelectedListener());
        placeAutocompleteFragment2.setOnPlaceSelectedListener(getDestPlaceSelectionListener());
        queue = Volley.newRequestQueue(this);

    }

    private PlaceSelectionListener getDestPlaceSelectionListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place selected: " + place.getName());
                destLatLong = place.getLatLng();
                placeIdDest = place.getId();
                googleMap.addMarker(new MarkerOptions().position(destLatLong).title("Source"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLong, 12));
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        };
    }

    private PlaceSelectionListener getSourcePlaceSelectedListener() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place selected: " + place.getName());
                sourceLatLong = place.getLatLng();
                placeIdSource = place.getId();
                googleMap.addMarker(new MarkerOptions().position(sourceLatLong).title("Source"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLong, 12));
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                showToast("Location Permission not given by user !!");
            }
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionsArray, locationRequestCode);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        try {
                            Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                            String strAddress = address.getAddressLine(0);
                            placeAutocompleteFragment1.setText(strAddress);
                        } catch (Exception e) {
                            showToast("Something went wrong while getting address from coordinates: " + e.getMessage());
                            Log.d(TAG, e.getMessage());
                        }
                        showToast("Coordinates are: " + latitude + ", " + longitude);
                    } else {
                        showToast("Something went wrong!! Location object was Null !!");
                    }
                }
            });
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLocation:
                getUserLocation();
                break;
            case R.id.imgBtnSearch:
                search();
                break;
        }
    }

    private void search() {
        String baseUrl = "https://maps.googleapis.com/maps/api/directions/";
        String url = baseUrl + "json?mode=transit&alternatives=true&key=AIzaSyC-RFWQJU7U-qx4pLtqkx0o6ZulQjMMvSI" + "&origin=place_id:" + placeIdSource + "&destination=place_id:" + placeIdDest;
        Log.d(TAG, url);
        JsonObjectRequest directionsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TransitApiResponse transitApiResponse = new TransitApiResponse(response);
                        handleTransitApiResponse(transitApiResponse);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("Something went wrong: " + error.getMessage());
            }
        });
        queue.add(directionsRequest);
    }

    private void handleTransitApiResponse(TransitApiResponse transitApiResponse) {
        List<Route> routeList = transitApiResponse.getRouteList();
        for (Route route : routeList) {
            List<Leg> legsList = route.getLegsList();
            if (legsList.size() == 1) {
                Leg leg = legsList.get(0);
                List<Step> stepList = leg.getStepList();
                boolean isValidList = verifyValidityOfList(stepList);
                if (isValidList) {
                    Step specificStep = getSpecificStep(stepList);
                    stepsToBeDisplayedInPopUp.add(specificStep);
                }
            }
        }

        Log.d(TAG, "i was called");
        for (Step step : stepsToBeDisplayedInPopUp) {
            String polyLinePoints = step.getPolyLinePoints();
            String name = step.getTransitDetails().getName() + " - " + step.getTransitDetails().getShortName();
            Log.d(TAG, name + " , " + polyLinePoints);
        }
        showPopUp(stepsToBeDisplayedInPopUp);
    }

    private void showPopUp(List<Step> stepsToBeDisplayedInPopUp) {
        RoutesAdapter routesAdapter = new RoutesAdapter(stepsToBeDisplayedInPopUp, getRecyclerViewClickHandler());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_dialog);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routesAdapter);
        dialog.show();
    }

    private ClickHandler getRecyclerViewClickHandler() {
        return new ClickHandler() {
            @Override
            public OnClickListener onItemClick(final Step step) {
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showToast(step.getPolyLinePoints());
                        List<LatLng> list = decodePoly(step.getPolyLinePoints());
                        //LatLng latLng = new LatLng(step.getPolyLinePoints())
                        //googleMap.addPolyline(new PolylineOptions().add)
                        Polyline line = googleMap.addPolyline(new PolylineOptions().addAll(list).width(5).color(Color.RED));
                        googleMap.addPolyline(new PolylineOptions().addAll(list));
                    }
                };
            }
        };
    }

    private Step getSpecificStep(List<Step> stepList) {
        for (Step step : stepList) {
            String travelMode = step.getTravelMode();
            if (travelMode.equals("TRANSIT")) {
                String polyLinePoints = step.getPolyLinePoints();
                String name = step.getTransitDetails().getName();
                String shortName = step.getTransitDetails().getShortName();
                String fullName = name + " - " + shortName;
                HashMap<String, String> map = new LinkedHashMap<>();
                map.put(fullName, polyLinePoints);
                return step;
            }
        }
        return null;
    }

    private boolean verifyValidityOfList(List<Step> stepList) {
        int i = 0;
        for (Step step : stepList) {
            String travelMode = step.getTravelMode();
            if (travelMode.equals("TRANSIT")) {
                i++;
            }
        }
        return i == 1;//Return true if if TRANSIT occurs only once
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
