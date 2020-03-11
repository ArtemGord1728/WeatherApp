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
import com.weather.app.common.SharedPrefUtils;
import com.weather.app.model.ListInfo;
import com.weather.app.model.ListWeatherResults;
import com.weather.app.network.GPSTracker;
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
    private ArrayList<ListInfo> arrayList;

    private GPSTracker gpsTracker;
    private SharedPreferences sharedPreferences;

    public static final String SAVE_FLAG_1 = "FLAG_1";
    public static final String SAVE_FLAG_2 = "FLAG_2";
    public static final String PREFERENCES = "pref";

    
    @SuppressLint("CheckResult")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_cities, container, false);
        ButterKnife.bind(this, root);

        Retrofit retrofit = RetrofitClient.getRetrofit();
        OpenWeatherAPI openWeatherAPI = retrofit.create(OpenWeatherAPI.class);

        arrayList = new ArrayList<>();
        citiesAdapter = new CitiesAdapter(arrayList);

        gpsTracker = new GPSTracker(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        double lat = SharedPrefUtils.getInstance().getDouble(sharedPreferences, SAVE_FLAG_1, 0.0f);
        double lng = SharedPrefUtils.getInstance().getDouble(sharedPreferences, SAVE_FLAG_2, 0.0f);

        recyclerViewCities.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCities.setHasFixedSize(true);

        openWeatherAPI.getWeatherResultForTowns(String.valueOf(lat),
                        String.valueOf(lng),
                AppConstants.COUNT_TOWNS, AppConstants.APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ListWeatherResults>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ListWeatherResults listWeatherResults) {
                        ArrayList<ListInfo> listWeather = listWeatherResults.getList();
                        arrayList.addAll(listWeather);
                        citiesAdapter.setListCities(arrayList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
        recyclerViewCities.setAdapter(citiesAdapter);
        return root;
    }
}