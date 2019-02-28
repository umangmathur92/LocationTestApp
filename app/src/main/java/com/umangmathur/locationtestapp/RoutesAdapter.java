package com.umangmathur.locationtestapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {

    private List<Step> stepList;
    private ClickHandler clickHandler;

    public RoutesAdapter(List<Step> stepList, ClickHandler clickHandler) {
        this.stepList = stepList;
        this.clickHandler = clickHandler;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_row, null);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder routeViewHolder, int i) {
        Step step = stepList.get(i);
        TransitDetails transitDetails = step.getTransitDetails();
        String name = transitDetails.getName();
        String shortName = transitDetails.getShortName();
        routeViewHolder.txtName.setText(name + " - " + shortName);
        routeViewHolder.txtName.setOnClickListener(clickHandler.onItemClick(step));
    }

    @Override
    public int getItemCount() {
        Log.d("UMG","Size is: " + stepList.size());
        return stepList.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;

        RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
        }

    }

    public interface ClickHandler {
        OnClickListener onItemClick(Step step);
    }
}
