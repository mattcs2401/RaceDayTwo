package com.mcssoft.racedaytwo.adapter.meeting

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemMeetingDetailBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.ViewType.HEADER

/**
 * ViewHolder for the RaceMeeting detail.
 * @param binding: The layout view.
 * @param iViewHolder: Interface with the MeetingAdapter. */
class MeetingDetailViewHolder(private val binding: ListItemMeetingDetailBinding,
                              private val iViewHolder: IMeetingViewHolder)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    internal fun bind(mce: MeetingCacheEntity) {
        // Refer IMeetingViewHolder interface.
        this.mce = mce
        // Bind.
        binding.apply {
            idTvMeetingCode.text = mce.meetingCode
            idTvVenueName.text = mce.venueName
            idTvFirstTime.text = mce.meetingTime
            idTvAbandonedResponse.text = mce.abandoned
            idTvNumRacesResponse.text = mce.hiRaceNo
            // Weather related.
            if(mce.weatherDesc != "") {
                idTvWeather.text = mce.weatherDesc
                idTvTrackDesc.text = mce.trackDesc
                idTvTrackCond.text = mce.trackCond
            }

            idCvListItemMeetingDetail.setOnClickListener(this@MeetingDetailViewHolder)
            idArrowUp.setOnClickListener(this@MeetingDetailViewHolder)

//            val view = idViewStub.layoutInflater.inflate(R.layout.meeting_race_summary, binding.root)
//            val rv = view.findViewById<RecyclerView>(R.id.id_meeting_race_summary)
//
//            val bp = "bp"
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.id_arrow_up ->
                iViewHolder.onExpandCollapseSelect(HEADER.ordinal, bindingAdapterPosition)

            else -> iViewHolder.onSelect(mce)
        }
    }

    private lateinit var mce: MeetingCacheEntity
}