package com.weather.app.ui.cities_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weather.app.R;
import com.weather.app.common.TinyDB;
import com.weather.app.model.ListWeatherInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.OpenWeatherAPI;
import com.weather.app.network.RetrofitClient;
import com.weather.app.common.AppConstants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ListCitiesFragment extends Fragment
{
    @BindView(R.id.recycler_view_list_of_city)
    RecyclerView recyclerViewCities;

    CitiesAdapter citiesAdapter;
    private ArrayList<ListWeatherInfo> arrayList;

    private TinyDB tinyDB;


    @SuppressLint("CheckResult")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_cities, container, false);
        ButterKnife.bind(this, root);

        Retrofit retrofit = RetrofitClient.getRetrofit();
        OpenWeatherAPI openWeatherAPI = retrofit.create(OpenWeatherAPI.class);

        arrayList = new ArrayList<>();
        citiesAdapter = new CitiesAdapter(arrayList);

        tinyDB = new TinyDB(getActivity());

        recyclerViewCities.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCities.setHasFixedSize(true);

        ArrayList<ListWeatherInfo> listWeather = tinyDB.getListObject("key", ListWeatherInfo.class);
        arrayList.addAll(listWeather);
        citiesAdapter.setListCities(arrayList);

        recyclerViewCities.setAdapter(citiesAdapter);
        return root;
    }
}