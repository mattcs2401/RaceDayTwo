package com.mcssoft.racedaytwo.adapter.meeting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.databinding.ListItemMeetingDetailBinding
import com.mcssoft.racedaytwo.databinding.ListItemMeetingHeaderBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE_DETAIL
import com.mcssoft.racedaytwo.utility.Constants.VIEW_TYPE_HEADER

/**
 * Class implements the RaceMeeting list adapter.
 */
class MeetingAdapter(private val iMeetingAdapter: IMeetingAdapter)
    : ListAdapter<MeetingCacheEntity, RecyclerView.ViewHolder>(MeetingDiffCallback()), IMeetingViewHolder {

    //https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        Log.d("TAG","MeetingAdapter.onCreateViewHolder")
        return when(viewType) {
            VIEW_TYPE_HEADER -> {
                MeetingHeaderViewHolder(
                    ListItemMeetingHeaderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false), this)
            }
            VIEW_TYPE_DETAIL -> {
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
            VIEW_TYPE_HEADER -> {
                holder as MeetingHeaderViewHolder
                holder.bind(getItem(position))
            }
            VIEW_TYPE_DETAIL -> {
                holder as MeetingDetailViewHolder
                holder.bind(getItem(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isExpanded) {
            true -> VIEW_TYPE_DETAIL
            false -> VIEW_TYPE_HEADER
        }
        //return super.getItemViewType(position)
    }

    //<editor-fold default state="collapsed" desc="Region: IMeetingViewHolder">
    override fun onExpandCollapseSelect(vhType: Int, position: Int) {
        when(vhType) {
            VIEW_TYPE_HEADER -> getItem(position).isExpanded = false
            VIEW_TYPE_DETAIL -> getItem(position).isExpanded = true
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

