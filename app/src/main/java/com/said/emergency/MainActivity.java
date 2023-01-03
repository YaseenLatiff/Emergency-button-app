package com.said.emergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.said.emergency.databinding.ActivityMainBinding;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavView =findViewById(R.id.bottom_nav_view);
        AppBarConfiguration appConfig = new AppBarConfiguration.Builder(R.id.panic_Fragment,R.id.edit_Fragment).build();
        NavController navCont = Navigation.findNavController(this,R.id.nav_fragment);
        NavigationUI.setupActionBarWithNavController(this,navCont,appConfig);
        NavigationUI.setupWithNavController(binding.bottomNavView, navCont);
    }
}