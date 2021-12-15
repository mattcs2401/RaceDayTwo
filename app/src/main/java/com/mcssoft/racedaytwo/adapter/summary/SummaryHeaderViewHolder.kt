package com.mcssoft.racedaytwo.adapter.summary

import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemSummaryHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.SummaryCacheEntity

class SummaryHeaderViewHolder(private val binding: ListItemSummaryHeaderBinding)
    : RecyclerView.ViewHolder(binding.root) {//}, View.OnClickListener {

    internal fun bind(sce: SummaryCacheEntity) {
        // TBA - local copy.
        this.summaryCacheEntity = sce
        // Bind.
        binding.apply {
            idTvMeetingCode.text = sce.meetingCode
            idTvRaceNum.text = sce.raceNo
            idTvRaceTime.text = sce.raceTime
            idTvVenueName.text = sce.venueName
            idTvRunnerNum.text = sce.runnerNo
            idTvRunnerName.text = sce.runnerName
//            idCvListItemSummaryHeader.setOnClickListener(this@SummaryHeaderViewHolder)
        }
    }

//    override fun onClick(view: View) {
//        // TBA
//    }

    private lateinit var summaryCacheEntity: SummaryCacheEntity
}
