@file:Suppress("ReplaceCallWithBinaryOperator")

package com.mcssoft.racedaytwo.adapter.race

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemRaceHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity

class RaceAdapter(private val adapter: IRaceAdapter): ListAdapter<RaceCacheEntity, RecyclerView.ViewHolder>(RaceDiffCallback()), IRaceViewHolder {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RaceHeaderViewHolder(
                   ListItemRaceHeaderBinding.inflate(
                       LayoutInflater.from(parent.context), parent, false), this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as RaceHeaderViewHolder
        holder.bind(getItem(position))
    }

    /**
     * Hand back to the RacesFragment.
     * @param race: The Race.
     */
    override fun onRaceSelect(race: RaceCacheEntity) {
        adapter.onRaceSelected(race)
    }
}

//<editor-fold default state="collapsed" desc="Region: DiffCallback">
private class RaceDiffCallback : DiffUtil.ItemCallback<RaceCacheEntity>() {

    override fun areItemsTheSame(oldItem: RaceCacheEntity, newItem: RaceCacheEntity): Boolean {
        return oldItem .mtgId == newItem.mtgId
    }

    override fun areContentsTheSame(oldItem: RaceCacheEntity, newItem: RaceCacheEntity): Boolean {
        return oldItem.equals(newItem)
    }
}
//</editor-fold>