package com.mcssoft.racedaytwo.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SummaryActivityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryActivity : AppCompatActivity() {

    private lateinit var appBarLayout: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SummaryActivityBinding.inflate(layoutInflater).apply {
            setContentView(root)
            setSupportActionBar(idToolbarSummary)
            appBarLayout = idAppBarLayoutSummary
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)     // so displays fragment's title.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)       // back nav.

        appBarLayout.findViewById<Toolbar>(R.id.id_toolbar_summary).apply {
            title = resources.getString(R.string.summary_fragment_name)
        }
    }

}