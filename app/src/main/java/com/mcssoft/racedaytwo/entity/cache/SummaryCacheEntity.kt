package com.mcssoft.racedaytwo.entity.cache

class SummaryCacheEntity {

    var id: Long? = null

    var meetingCode: String = ""
    var venueName: String = ""

    var raceNo: String = ""
    var raceTime: String = ""

    var runnerId: Long? = null         // not displayed.
    var runnerNo: String = ""
    var runnerName: String = ""
    //
    var elapsed: Boolean = false       // flag, race time is now in the past.
    var notify: Boolean = false      // flag, notification done.

    var colour: Int? = null            // background colour.

}