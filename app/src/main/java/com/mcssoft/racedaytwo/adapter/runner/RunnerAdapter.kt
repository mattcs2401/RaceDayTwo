package com.mcssoft.racedaytwo.adapter.runner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemRunnerHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.RunnerCacheEntity
import com.mcssoft.racedaytwo.entity.events.SelectedRunner

class RunnerAdapter(private val adapter: IRunnerAdapter) :
    ListAdapter<RunnerCacheEntity, RecyclerView.ViewHolder>(RunnerDiffCallback()),
    IRunnerViewHolder {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RunnerHeaderViewHolder(
            ListItemRunnerHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false), this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as RunnerHeaderViewHolder
        holder.bind(getItem(position))
    }

    /**
     * Hand back to the RunnersFragment.
     * @param selectedRunner: The Runner.
     */
    override fun onRunnerSelected(selectedRunner: SelectedRunner) {
        adapter.onRunnerSelected(selectedRunner)
    }
}
//<editor-fold default state="collapsed" desc="Region: DiffCallback">
private class RunnerDiffCallback : DiffUtil.ItemCallback<RunnerCacheEntity>() {

    override fun areItemsTheSame(oldItem: RunnerCacheEntity, newItem: RunnerCacheEntity): Boolean {
        return oldItem.runnerNo == newItem.runnerNo
    }

    override fun areContentsTheSame(oldItem: RunnerCacheEntity, newItem: RunnerCacheEntity): Boolean {
        return oldItem.runnerNo == newItem.runnerNo
        // TODO - add the rest of the comparison elements.
    }
}