package com.mcssoft.racedaytwo.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {//}, IPopBack {

    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
//    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG","[MainActivity.onCreate]")

        // Set view binding.
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar.
        setSupportActionBar(binding.idToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Navigation.
        navController = Navigation.findNavController(this, R.id.id_nav_host_fragment)

        /* Notes:
          (1) This makes the MainFragment a top level destination. App starts with SplashFragment
              and then navigation moves to MainFragment. We don't want a back arrow from MainFragment.
              That is also trapped with an on back press callback.
          (2) Trial and error - also puts the "<-" Up nav button on the PreferencesFragment ??
        */
        val appBarConfig = AppBarConfiguration(setOf(R.id.id_main_fragment))

        // TBA - bottom navigation view ?
//        bottomNavView = binding.idBottomNavView
//        NavigationUI.setupWithNavController(bottomNavView, navController)
//        bottomNavView.setOnNavigationItemSelectedListener(this)

        // Back navigation is basically restricted by the AppBarConfiguration.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
    }

}