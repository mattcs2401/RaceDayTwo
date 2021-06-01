package com.mcssoft.racedaytwo.entity.cache

/**
 * Class to model a RaceDay cache related entity. Pretty much the same as the database entity but
 * includes a flag for recyclerview item expand purposes.
 */
class RaceMeetingCacheEntity {
    var id: Long? = null               // just mapped, use TBA.
    var mtgId: String = ""
    var meetingCode: String = ""
    var meetingType: String = ""
    var meetingName: String = ""
    var abandoned: String = ""

    // Adapter flag, if true, signifies an adapter view type change.
    var meta: Boolean = false

//    var weatherChanged: String = ""
//    var venueName: String = ""
//    var hiRaceNo: String = ""
//    var trackChanged: String = ""
//    var nextRaceNo: String = ""
//    var sortOrder: String = ""

}