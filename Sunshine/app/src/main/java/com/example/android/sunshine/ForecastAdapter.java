package com.example.android.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

/**
 * Created by MY on 2017-01-03.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{
    private ForecastAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private Cursor mCursor;

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View itemView) {
            super(itemView);

            mWeatherTextView = (TextView) itemView.findViewById(R.id.tv_weather_data);

            itemView.setOnClickListener(this);
        }

        public void bind(int index) {
            mCursor.moveToPosition(index);

            long milliSeconds = mCursor.getLong(MainActivity.INDEX_DATE);
            String normalDate = SunshineDateUtils.getFriendlyDateString(mContext, milliSeconds, false);

            int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_ID);
            String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);

            double minTemp = mCursor.getDouble(MainActivity.INDEX_MIN_TEMP);
            double maxTemp = mCursor.getDouble(MainActivity.INDEX_MAX_TEMP);
            String bothTemp = SunshineWeatherUtils.formatHighLows(mContext, maxTemp, minTemp);

            mWeatherTextView.setText(normalDate + " - " + description + " - " + bothTemp);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long date = mCursor.getLong(MainActivity.INDEX_DATE);
            mClickHandler.access(date);
        }
    }

    interface ForecastAdapterOnClickHandler {
        public void access(long date);
    }

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int listId = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean immd = false;

        View view = inflater.inflate(listId, viewGroup, immd);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;

        notifyDataSetChanged();
    }
}
