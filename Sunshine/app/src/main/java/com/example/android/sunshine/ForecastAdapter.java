package com.example.android.sunshine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by MY on 2017-01-03.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);

            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);
        }

        public void bind(int index) {
            mWeatherTextView.setText(mWeatherData[index]);
        }
    }

    private String[] mWeatherData;

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int listId = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean imm = false;

        View view = inflater.inflate(listId, viewGroup, imm);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mWeatherData != null) {
            return mWeatherData.length;
        } else {
            return 0;
        }
    }

    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;

        notifyDataSetChanged();
    }
}
