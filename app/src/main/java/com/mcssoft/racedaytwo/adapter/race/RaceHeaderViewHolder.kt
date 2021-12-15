package com.mcssoft.racedaytwo.adapter.race

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemRaceHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity

class RaceHeaderViewHolder(private val binding: ListItemRaceHeaderBinding,
                           private val iViewHolder: IRaceViewHolder )
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    internal fun bind(raceCacheEntity: RaceCacheEntity) {
        // TBA.
        this.raceCacheEntity = raceCacheEntity
        // Bind.
        binding.apply {
            idTvRaceNo.text = raceCacheEntity.raceNo
            idTvRaceName.text = raceCacheEntity.raceName
            idTvRaceTime.text = raceCacheEntity.raceTime
            idTvRaceDistance.text = raceCacheEntity.distance
            idCvListItemRaceHeader.setOnClickListener(this@RaceHeaderViewHolder)
        }
    }

    override fun onClick(view: View) {
        iViewHolder.onRaceSelect(raceCacheEntity)
    }

    private lateinit var raceCacheEntity: RaceCacheEntity
}