package com.mcssoft.racedaytwo.utility

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcssoft.racedaytwo.R
import javax.inject.Inject
import com.mcssoft.racedaytwo.utility.NavManager.NMView.BOTTOM_NAV_VIEW
import com.mcssoft.racedaytwo.utility.NavManager.NMView.APP_BAR_VIEW

/**
 * Utility class to enable or disable navigation related elements of the UI depending on what
 * fragment is displaying.
 */
class NavManager @Inject constructor() {

    /**
     * Nav manager specific views.
     */
    enum class NMView {
        BOTTOM_NAV_VIEW,     // bottom navigation menu view.
        APP_BAR_VIEW         // the app bar that contains the tool bar.
    }

    private lateinit var bnView: BottomNavigationView      // bottom nav view.
    private lateinit var abView: AppBarLayout              // app bar view.
    private lateinit var _tbView: Toolbar                  // tool bar view.

    private lateinit var homeMenu: MenuItem
    private lateinit var summaryMenu: MenuItem
    private lateinit var settingsMenu: MenuItem
    private lateinit var refreshMenu: MenuItem

    var tbView: Toolbar                     // expose the toolbar as a property.
        get() = _tbView
        set(value) { _tbView = value }

    /**
     * Set the view variables.
     * @param NMView: The name of the view to set.
     * @param view: The view value.
     * @note This method must be called first.
     */
    fun setView(NMView: NMView, view: View) = when(NMView) {
        BOTTOM_NAV_VIEW -> {
            bnView = view as BottomNavigationView
            refreshMenu = bnView.menu.findItem(R.id.id_mnu_bnv_refresh)
            settingsMenu = bnView.menu.findItem(R.id.id_mnu_bnv_settings)
            summaryMenu = bnView.menu.findItem(R.id.id_mnu_bnv_summary)
            homeMenu = bnView.menu.findItem(R.id.id_mnu_bnv_home)
        }
        APP_BAR_VIEW -> {
            abView = view as AppBarLayout
            // also set the toolbar.
            _tbView = abView.findViewById(R.id.id_toolbar)
        }
    }

    /**
     * Hide a view.
     * @param NMView: The name of the view to hide.
     * @param hide: True to make the view invisible, else, visible.
     */
    fun hideView(NMView: NMView, hide: Boolean) = when(NMView) {
        BOTTOM_NAV_VIEW -> {
            if(hide) bnView.visibility = View.INVISIBLE else bnView.visibility = View.VISIBLE
        }
        APP_BAR_VIEW -> {
            if(hide) abView.visibility = View.INVISIBLE else abView.visibility = View.VISIBLE
        }
    }

    /**
     * Check the visibility status of a view.
     * @param NMView: The name of the view to check.
     */
    fun viewVisible(NMView: NMView): Boolean {
        return when(NMView) {
            BOTTOM_NAV_VIEW -> bnView.isVisible
            APP_BAR_VIEW -> abView.isVisible
        }
    }

    /**
     * Convenience method.
     */
    fun disableAllButHome() {
        homeMenu.isEnabled = true
        summaryMenu.isEnabled = false
        settingsMenu.isEnabled = false
        refreshMenu.isEnabled = false
    }

    /**
     * Convenience method.
     */
    fun enableAllButHome() {
        homeMenu.isEnabled = false
        summaryMenu.isEnabled = true
        settingsMenu.isEnabled = true
        refreshMenu.isEnabled = true
    }

}