package com.mcssoft.racedaytwo.adapter.summary

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemSummaryHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity

class SummaryHeaderViewHolder(private val binding: ListItemSummaryHeaderBinding)
    : RecyclerView.ViewHolder(binding.root) {

    internal fun bind(sce: SummaryCacheEntity) {
        // Bind.
        binding.apply {
            idTvMeetingCode.text = sce.meetingCode
            idTvRaceNum.text = sce.raceNo
            idTvRaceTime.text = sce.raceTime
            idTvVenueName.text = sce.venueName
            idTvRunnerNum.text = sce.runnerNo
            idTvRunnerName.text = sce.runnerName
            // Background colour.
            idCvListItemSummaryHeader.setCardBackgroundColor(sce.colour!!)
        }
    }

}
