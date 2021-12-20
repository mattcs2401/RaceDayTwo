package com.mcssoft.racedaytwo.adapter.runner

import android.graphics.Color
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemRunnerHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.events.SelectedRunner

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
            if (rce.scratched == "Y") {
                idTvRunnerName.setTextColor(Color.RED)
                idCbRunnerSel.isEnabled = false
            } else {
                if(rce.selected) {
                    idCbRunnerSel.isChecked = true
                }
                idCbRunnerSel.setOnCheckedChangeListener(this@RunnerHeaderViewHolder)
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

    private lateinit var selectedRunner: SelectedRunner
    private lateinit var runnerCacheEntity: RunnerCacheEntity
}

