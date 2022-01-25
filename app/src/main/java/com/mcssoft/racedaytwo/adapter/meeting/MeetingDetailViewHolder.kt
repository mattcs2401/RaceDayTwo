package com.mcssoft.racedaytwo.adapter.meeting

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemMeetingDetailBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE.HEADER

/**
 * ViewHolder for the RaceMeeting detail.
 * @param binding: The layout view.
 * @param iViewHolder: Interface with the MeetingAdapter. */
class MeetingDetailViewHolder(private val binding: ListItemMeetingDetailBinding,
                              private val iViewHolder: IMeetingViewHolder)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    internal fun bind(meetingCacheEntity: MeetingCacheEntity) {
        // Refer IMeetingViewHolder interface.
        this.meetingCacheEntity = meetingCacheEntity
        // Bind.
        binding.apply {
            idTvMeetingCode.text = meetingCacheEntity.meetingCode
            idTvVenueName.text = meetingCacheEntity.venueName
            idTvFirstTime.text = meetingCacheEntity.meetingTime
            idTvAbandonedResponse.text = meetingCacheEntity.abandoned
            idTvNumRacesResponse.text = meetingCacheEntity.hiRaceNo
            // Weather related.
            if(meetingCacheEntity.weatherDesc != "") {
                idTvWeather.text = meetingCacheEntity.weatherDesc
                idTvTrackDesc.text = meetingCacheEntity.trackDesc
                idTvTrackCond.text = meetingCacheEntity.trackCond
            }

            idCvListItemMeetingDetail.setOnClickListener(this@MeetingDetailViewHolder)
            idArrowUp.setOnClickListener(this@MeetingDetailViewHolder)
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.id_arrow_up ->
                iViewHolder.onExpandCollapseSelect(HEADER.ordinal, bindingAdapterPosition)

            else -> iViewHolder.onSelect(meetingCacheEntity)
        }
    }

    private lateinit var meetingCacheEntity: MeetingCacheEntity
}