package com.mcssoft.racedaytwo.utility.parser

import android.content.Context
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.DataResult
import com.mcssoft.racedaytwo.utility.Downloader
import java.io.InputStream

/**
 * A utility class to parse the xml within the given file.
 * @param context For access to string resources.
 * @param fName The name of the file to parse.
 */
class RaceDayParser(private val context: Context, fName: String = ""): BaseParser() {
    /* https://extendsclass.com/xpath-tester.html
    * https://www.freeformatter.com/xpath-tester.html <-- gives expression examples
    */

    // The input stream. Basically the Xml based file contents that will be parsed.
    private lateinit var stream: InputStream

    init {
        if(fName != "") {
            stream = Downloader(context).getFileAsStream(fName)
        }
    }

    fun parseForMeetingsAndRaces(): DataResult<ArrayList<MutableMap<String, String>>> {
        val expr = context.resources.getString(R.string.meetings_races_parse_path)
        return parse(expr, stream)
    }

    fun parseForRacesAndRunners(): DataResult<ArrayList<MutableMap<String, String>>> {
        val expr = context.resources.getString(R.string.races_runners_parse_path)
        return parse(expr, stream)
    }

    /**
     * The the input stream based on the file name.
     * @param name: The file name.
     */
    fun setStream(name: String) {
        stream = Downloader(context).getFileAsStream(name)
    }

}


