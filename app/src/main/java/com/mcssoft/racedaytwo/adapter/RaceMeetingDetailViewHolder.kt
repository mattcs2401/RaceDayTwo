package com.mcssoft.racedaytwo.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.ListItemMeetingDetailBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.interfaces.IViewHolder
import com.mcssoft.racedaytwo.utility.Constants

/**
 * ViewHolder for the RaceMeeting detail.
 * @param binding: The layout view.
 */
class RaceMeetingDetailViewHolder(private val binding: ListItemMeetingDetailBinding, private val iViewHolder: IViewHolder)
    : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        binding.clickListener = this
        binding.detailsClickListener = this
    }

    internal fun bind(mtg: MeetingCacheEntity) {
        binding.apply {
            meeting = mtg

            executePendingBindings()
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.id_tv_details -> {
                iViewHolder.onDetailsSelect()
            } else -> {
                iViewHolder.onViewHolderSelect(Constants.VIEW_TYPE_HEADER, adapterPosition)
            }
        }
    }

}