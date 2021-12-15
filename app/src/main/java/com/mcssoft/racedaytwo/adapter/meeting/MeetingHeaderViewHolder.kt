package com.mcssoft.racedaytwo.adapter.meeting

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemMeetingHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE_DETAIL

/**
 * ViewHolder for the RaceMeeting header.
 * @param binding: The layout view.
 * @param iViewHolder: Interface with the MeetingAdapter.
 */
class MeetingHeaderViewHolder(private val binding: ListItemMeetingHeaderBinding,
                              private val iViewHolder: IMeetingViewHolder)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    internal fun bind(meetingCacheEntity: MeetingCacheEntity) {
        binding.apply {
            idTvMeetingCode.text = meetingCacheEntity.meetingCode
            idTvVenueName.text = meetingCacheEntity.venueName
            idTvFirstTime.text = meetingCacheEntity.meetingTime
            idArrowDown.setOnClickListener(this@MeetingHeaderViewHolder)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.id_arrow_down ->
                iViewHolder.onExpandCollapseSelect(VIEW_TYPE_DETAIL, bindingAdapterPosition)
        }
    }

}