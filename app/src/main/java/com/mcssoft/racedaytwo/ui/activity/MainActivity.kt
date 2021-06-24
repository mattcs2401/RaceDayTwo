package com.mcssoft.racedaytwo.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.MainActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {//}, IPopBack {

    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG","MainActivity.onCreate")

        // Set view binding.
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar.
        setSupportActionBar(binding.idToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Navigation.
        navController = Navigation.findNavController(this, R.id.id_nav_host_fragment)

        // Note:
        // This makes these top level destinations. App starts with SplashFragment and when done,
        // navigation moves to MainFragment. We don't want a back arrow from MainFragment.
        val appBarConfig = AppBarConfiguration(
            setOf(R.id.id_splash_fragment, R.id.id_main_fragment)
        )

        // TODO - implement bottom navigation view ?
//        bottomNavView = binding.idBottomNavView
//        NavigationUI.setupWithNavController(bottomNavView, navController)
//        bottomNavView.setOnNavigationItemSelectedListener(this)

        // Back navigation is basically restricted by the AppBarConfiguration.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
    }

//    override fun onStart() {
//        super.onStart()
//        Log.d("TAG","MainActivity.onStart")
//    }

//    override fun onStop() {
//        super.onStop()
//        Log.d("TAG","MainActivity.onStop")
//    }

}