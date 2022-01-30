package com.mcssoft.racedaytwo.adapter.meeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemMeetingDetailBinding
import com.mcssoft.racedaytwo.databinding.ListItemMeetingHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE.DETAIL
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE.HEADER

/**
 * Class implements the RaceMeeting list adapter.
 */
class MeetingAdapter(private val iMeetingAdapter: IMeetingAdapter)
    : ListAdapter<MeetingCacheEntity, RecyclerView.ViewHolder>(MeetingDiffCallback()), IMeetingViewHolder {

    //https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter

    // TODO - try this one WRT getItemViewType:
    //  https://www.simplifiedcoding.net/recyclerview-with-multiple-view-types/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        Log.d("TAG","MeetingAdapter.onCreateViewHolder")
        return when(viewType) {
            HEADER.ordinal -> {
                MeetingHeaderViewHolder(
                    ListItemMeetingHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false), this)
            }
            DETAIL.ordinal -> {
                MeetingDetailViewHolder(
                    ListItemMeetingDetailBinding.inflate(
                            LayoutInflater.from(parent.context), parent, false), this)
            }
            else -> throw ClassCastException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Log.d("TAG","[MeetingAdapter.onBindViewHolder]")
        when(getItemViewType(position)) {
            HEADER.ordinal -> {
                holder as MeetingHeaderViewHolder
                holder.bind(getItem(position))
            }
            DETAIL.ordinal -> {
                holder as MeetingDetailViewHolder
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isExpanded) {
            true -> DETAIL.ordinal
            false -> HEADER.ordinal
        }
        //return super.getItemViewType(position)
    }

    //<editor-fold default state="collapsed" desc="Region: IMeetingViewHolder">
    override fun onExpandCollapseSelect(vhType: Int, position: Int) {
        when(vhType) {
            HEADER.ordinal -> getItem(position).isExpanded = false
            DETAIL.ordinal -> getItem(position).isExpanded = true
        }
        notifyItemChanged(position)
    }

    override fun onSelect(meeting: MeetingCacheEntity) {
        // Hand back to the MeetingsFragment.
        iMeetingAdapter.onSelected(meeting)
    }
    //</editor-fold>
}

//<editor-fold default state="collapsed" desc="Region: DiffCallback">
private class MeetingDiffCallback : DiffUtil.ItemCallback<MeetingCacheEntity>() {

    override fun areItemsTheSame(oldItem: MeetingCacheEntity, newItem: MeetingCacheEntity): Boolean {
        return oldItem.meetingId == newItem.meetingId
    }

    override fun areContentsTheSame(oldItem: MeetingCacheEntity, newItem: MeetingCacheEntity): Boolean {
        return oldItem.equals(newItem)
    }
}
//</editor-fold>

