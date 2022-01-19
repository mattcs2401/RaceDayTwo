package com.mcssoft.racedaytwo.entity.cache

class RunnerCacheEntity {
    var id: Long? = null          // value inserted by Room.

    var raceId: Long? = null      //
    var raceTime: String = ""     //
    var runnerNo: String = ""     //
    var runnerName: String = ""   //
    var barrier: String = ""      //
    var scratched: String = ""    //

    // Flag, signifies list item selected in the display of Runners.
    var selected: Boolean = false     //
}
