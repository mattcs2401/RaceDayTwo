package com.mcssoft.racedaytwo.adapter.runner

import android.graphics.Color
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemRunnerHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.events.SelectedRunner
import com.mcssoft.racedaytwo.utility.Constants.HOUR
import com.mcssoft.racedaytwo.utility.Constants.MINUTE
import java.util.*

class RunnerHeaderViewHolder(private val binding: ListItemRunnerHeaderBinding,
                             private val iViewHolder: IRunnerViewHolder)
    : RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {

    internal fun bind(rce: RunnerCacheEntity) {
        // TBA.
        runnerCacheEntity = rce
        // Bind.
        binding.apply {
            idTvRunnerNo.text = rce.runnerNo
            idTvRunnerName.text = rce.runnerName
            when {
                rce.scratched == "Y" -> {
                    idTvRunnerName.setTextColor(Color.RED)
                    idCbRunnerSel.isEnabled = false
                }
                compareToCurrentTime(rce.raceTime) -> {
                    // The current time is greater than the Race time.
                    idTvRunnerName.setTextColor(Color.LTGRAY)
                    idCbRunnerSel.isEnabled = false
                }
                else -> {
                    if(rce.selected) {
                        idCbRunnerSel.isChecked = true
                    }
                    idCbRunnerSel.setOnCheckedChangeListener(this@RunnerHeaderViewHolder)
                }
            }
        }
    }

    override fun onCheckedChanged(view: CompoundButton, isChecked: Boolean) {
        when(view.id) {
            R.id.id_cb_runner_sel -> {
                selectedRunner = SelectedRunner(binding.idTvRunnerNo.text.toString())
                when (isChecked) {
                    true -> selectedRunner.selected = true
                    false -> selectedRunner.selected = false
                }
                // Send back to Cache via Adapter -> Fragment -> ViewModel.
                iViewHolder.onRunnerSelected(selectedRunner)
            }
        }
    }

    /**
     * Compare a given time (formatted as HH:MM) to the current time.
     * @param formattedTime: A time value as HH:MM.
     * @return True if the current time is greater than the time given, else false.
     * @notes Can't inject the DateUtilities into this ViewHolder.
     */
    private fun compareToCurrentTime(formattedTime: String): Boolean {
        val time = formattedTime.split(":")
        val givenTime = Calendar.getInstance(Locale.getDefault()).apply {
            set(Calendar.HOUR_OF_DAY, time[HOUR].toInt())
            set(Calendar.MINUTE, time[MINUTE].toInt())
        }
        return Calendar.getInstance(Locale.getDefault()).timeInMillis > givenTime.timeInMillis
    }

    private lateinit var selectedRunner: SelectedRunner
    private lateinit var runnerCacheEntity: RunnerCacheEntity
}

