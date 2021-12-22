package com.mcssoft.racedaytwo.adapter.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemSummaryHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity

class SummaryAdapter :
    ListAdapter<SummaryCacheEntity, RecyclerView.ViewHolder>(SummaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SummaryHeaderViewHolder(
            ListItemSummaryHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as SummaryHeaderViewHolder
        holder.bind(getItem(position))
    }

}
//<editor-fold default state="collapsed" desc="Region: DiffCallback">
private class SummaryDiffCallback : DiffUtil.ItemCallback<SummaryCacheEntity>() {

    override fun areItemsTheSame(oldItem: SummaryCacheEntity, newItem: SummaryCacheEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SummaryCacheEntity, newItem: SummaryCacheEntity): Boolean {
        return oldItem.equals(newItem)
    }
}