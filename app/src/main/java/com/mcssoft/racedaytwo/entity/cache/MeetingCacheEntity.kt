package com.mcssoft.racedaytwo.entity.cache

import android.os.Parcel
import android.os.Parcelable

/**
 * Class to model a RaceDay cache related entity. Pretty much the same as the database entity but
 * includes a flag for recyclerview item expand purposes.
 * @note: Class implements Parcelable as it's passed as a NavArgs to the RacesFragment.
 */
class MeetingCacheEntity() : Parcelable {
    var id: Long? = null          // just mapped, use TBA.
    var meetingId: String = ""        // "    "       "   "
    var meetingCode: String = ""  //
    var meetingType: String = ""  //
    var venueName: String = ""    //
    var abandoned: String = ""    //
    var hiRaceNo: String = ""     //

    // Adapter flag, if true, the view in the adapter is in an expanded view.
    var isExpanded: Boolean = false

    // Derived from Race related info.
    var meetingDate: String = ""  // from date part of RaceTime (1st Race).
    var meetingTime: String = ""  // from time part of RaceTime (1st Race).
    var trackDesc: String = ""    // e.g. Soft
    var trackCond: String = ""    // e.g. 4
    var weatherDesc: String = ""  // e.g. Overcast.

    //<editor-fold default state="collapsed" desc="Region: Parcel">
    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Long::class.java.classLoader) as? Long
        meetingId = parcel.readString()!!
        meetingCode = parcel.readString()!!
        meetingType = parcel.readString()!!
        venueName = parcel.readString()!!
        abandoned = parcel.readString()!!
        hiRaceNo = parcel.readString()!!
        isExpanded = parcel.readByte() != 0.toByte()
        meetingDate = parcel.readString()!!
        meetingTime = parcel.readString()!!
        trackDesc = parcel.readString()!!
        trackCond = parcel.readString()!!
        weatherDesc = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(meetingId)
        parcel.writeString(meetingCode)
        parcel.writeString(meetingType)
        parcel.writeString(venueName)
        parcel.writeString(abandoned)
        parcel.writeString(hiRaceNo)
        parcel.writeByte(if (isExpanded) 1 else 0)
        parcel.writeString(meetingDate)
        parcel.writeString(meetingTime)
        parcel.writeString(trackDesc)
        parcel.writeString(trackCond)
        parcel.writeString(weatherDesc)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MeetingCacheEntity> {
        override fun createFromParcel(parcel: Parcel): MeetingCacheEntity {
            return MeetingCacheEntity(parcel)
        }

        override fun newArray(size: Int): Array<MeetingCacheEntity?> {
            return arrayOfNulls(size)
        }
    }
    //</editor-fold>
}