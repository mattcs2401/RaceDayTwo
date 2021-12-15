package com.mcssoft.racedaytwo.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.MainActivityBinding
import com.mcssoft.racedaytwo.ui.dialog.IRefresh
import com.mcssoft.racedaytwo.ui.dialog.RefreshDialog
import com.mcssoft.racedaytwo.ui.fragment.MeetingsFragmentDirections
import com.mcssoft.racedaytwo.utility.Alarm
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, IRefresh {

    @Inject lateinit var alarm: Alarm

    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(idToolbar)
            bottomNavView = idBottomNavView
            bottomNavView.visibility = View.INVISIBLE
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /* Notes:
          This makes the MeetingsFragment a top level destination. App starts with the
          SplashFragment and then the navigation moves to MeetingsFragment. We don't want a back
          arrow from the MeetingsFragment. That is also trapped with an on back press callback.
        */
        val appBarConfig = AppBarConfiguration(setOf(R.id.id_meetings_fragment))

        // Navigation.
        navController = Navigation.findNavController(this, R.id.id_nav_host_fragment)
        // Bottom navigation view.
        NavigationUI.setupWithNavController(bottomNavView, navController)
        // Back navigation is basically restricted by the AppBarConfiguration.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)

        // Set listener for bottom nav menu items. Note: doesn't seem to work within the binding
        // code block.
        bottomNavView.setOnItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()
//        alarm.setAlarm(1)
    }

    override fun onStop() {
        super.onStop()
//        alarm.cancelAlarm()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.id_mnu_bnv_home -> {
                if(navController.currentDestination?.id != R.id.id_meetings_fragment) {
                    navController.navigate(R.id.id_meetings_fragment)
                }
            }
            R.id.id_mnu_bnv_refresh -> {
                val dialog = RefreshDialog(this)
                dialog.show(supportFragmentManager, resources.getString(R.string.tag_refresh_dialog))
            }
            R.id.id_mnu_bnv_summary -> {
                val action = MeetingsFragmentDirections.actionMeetingsFragmentToSummaryFragment()
                navController.navigate(action)
            }
            R.id.id_mnu_bnv_settings -> {
                val action = MeetingsFragmentDirections.actionMeetingsFragmentToSettingsFragment()
                navController.navigate(action)
            }
        }
        return true
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Interface - IRefresh">
    override fun refreshOk() {
        val action = MeetingsFragmentDirections.actionMeetingsFragmentToSplashFragment(true)
        navController.navigate(action)
    }
    //</editor-fold>
}