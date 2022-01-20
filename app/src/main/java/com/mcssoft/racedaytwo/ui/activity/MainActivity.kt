package com.mcssoft.racedaytwo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.MainActivityBinding
import com.mcssoft.racedaytwo.ui.dialog.IRefresh
import com.mcssoft.racedaytwo.ui.dialog.RefreshDialog
import com.mcssoft.racedaytwo.ui.fragment.MeetingsFragmentDirections
import com.mcssoft.racedaytwo.utility.Alarm
import com.mcssoft.racedaytwo.utility.NavManager
import com.mcssoft.racedaytwo.utility.NavManager.NMView
import com.mcssoft.racedaytwo.utility.NotifyUtilities
import com.mcssoft.racedaytwo.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener, IRefresh {

    @Inject lateinit var alarm: Alarm
    @Inject lateinit var navManager: NavManager
    @Inject lateinit var notifyUtils: NotifyUtilities
    @Inject lateinit var summaryViewModel: SummaryViewModel

    private lateinit var navController: NavController
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var appBarLayout: AppBarLayout

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(idToolbar)
            bottomNavView = idBottomNavView
            bottomNavView.visibility = View.INVISIBLE
            appBarLayout = idAppBarLayout
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

        // Set the views for the nav manager.
        navManager.apply {
            // Establish the views with the menu manager.
            setView(NMView.BOTTOM_NAV_VIEW, bottomNavView)
            setView(NMView.APP_BAR_VIEW, appBarLayout)
            // Hide the views to start with. The fragments will change the views.
            hideView(NMView.BOTTOM_NAV_VIEW, true)
            hideView(NMView.APP_BAR_VIEW, true)
        }
    }

    override fun onStart() {
        super.onStart()
        // Set notification channel to be used.
        notifyUtils.createNotificationChannel()
        // Check if Runners have been selected, i.e. there's something in the summary cache.
        checkForSelections()
    }

    override fun onStop() {
        // Cancel all previous notifications.
        notifyUtils.cancel()
        // Cancel the alarm.
        alarm.cancelAlarm()
        super.onStop()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.id_mnu_bnv_home -> {
                // Check if Runners have been selected, i.e. there's something in the summary cache.
                checkForSelections()
                // Nav to Meetings fragment.
                if (navController.currentDestination?.id != R.id.id_meetings_fragment) {
                    navController.navigate(R.id.id_meetings_fragment)
                }
                return true
            }
            R.id.id_mnu_bnv_refresh -> {
                val dialog = RefreshDialog(this)
                dialog.show(supportFragmentManager, resources.getString(R.string.tag_refresh_dialog))
                return true
            }
            R.id.id_mnu_bnv_summary -> {
//                val action =
//                    MeetingsFragmentDirections.actionMeetingsFragmentToSummaryFragment()
//                navController.navigate(action)
                val intent = Intent(this, SummaryActivity::class.java).apply {
                    // TBA putExtra(EXTRA_MESSAGE, message)
                }
                startActivity(intent)
                return true
            }
            R.id.id_mnu_bnv_settings -> {
                val action =
                    MeetingsFragmentDirections.actionMeetingsFragmentToSettingsFragment()
                navController.navigate(action)
                return true
            }
        }
        return false
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Interface - IRefresh">
    override fun refreshOk() {
        val action = MeetingsFragmentDirections.actionMeetingsFragmentToSplashFragment(true)
        navController.navigate(action)
    }
    //</editor-fold>

    /**
     * Check if the summary cache has elements. No point setting an alarm if there's nothing to
     * post a notification about.
     */
    private fun checkForSelections() {
        lifecycleScope.launchWhenStarted {
            summaryViewModel.getCountAsFlow().collectLatest { count ->
                if(count > 0) {
                    alarm.setAlarm(1)
                } else {
                    alarm.cancelAlarm()
                }
            }
        }
    }

}
