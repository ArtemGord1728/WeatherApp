package com.weather.app.ui.cities_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weather.app.R;
import com.weather.app.model.ListWeatherInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityViewHolder>
{
    private ArrayList<ListWeatherInfo> listCities;

    public CitiesAdapter(ArrayList<ListWeatherInfo> listCities) {
        this.listCities = listCities;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        ListWeatherInfo weatherResult = listCities.get(position);
        String isMoreThanZero = weatherResult.getMain().getTemp() > 0 ? "+" : "-";
        holder.city_tv.setText(new StringBuilder("Город: " + weatherResult.getName()));
        holder.temp_tv.setText(new StringBuilder("Темп-тура, °C \n" + isMoreThanZero + weatherResult.getMain().getTemp()));
        holder.weather_main_tv.setText(new StringBuilder("Влажность \n" + weatherResult.getMain().getHumidity()).append(" %"));
        holder.weather_pressure_tv.setText(new StringBuilder("Давление \n" + weatherResult.getMain().getPressure()).append(" hpa"));
    }

    public void setListCities(ArrayList<ListWeatherInfo> listCities) {
        this.listCities = listCities;
        notifyItemRangeInserted(0, listCities.size());
    }

    @Override
    public int getItemCount()
    {
        return listCities.size();
    }

    class CityViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tv_city)
        TextView city_tv;

        @BindView(R.id.tv_temperature)
        TextView temp_tv;

        @BindView(R.id.tv_weather_humidity)
        TextView weather_main_tv;

        @BindView(R.id.tv_weather_pressure)
        TextView weather_pressure_tv;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
