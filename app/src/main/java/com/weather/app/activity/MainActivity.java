package com.weather.app.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.weather.app.R;
import com.weather.app.ui.cities_list.ListCitiesFragment;
import com.weather.app.ui.map.MapFragment;

public class MainActivity extends AppCompatActivity
{
    private MapFragment mapFragment;
    private ListCitiesFragment listCitiesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        mapFragment = new MapFragment();
        listCitiesFragment = new ListCitiesFragment();

        getSupportFragmentManager().beginTransaction()
                .hide(mapFragment)
                .hide(listCitiesFragment)
                .commit();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_list_cities)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

}
