package com.mcssoft.racedaytwo.utility.parser

import android.content.Context
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.DataResult
import java.io.InputStream

class FirstRunParser(private val context: Context, fName: String): BaseParser() {

    private var stream: InputStream = context.resources.assets.open(fName)

    fun parseForFirstRun(): DataResult<ArrayList<MutableMap<String, String>>> {
        val expr = context.resources.getString(R.string.country_path)
        return parse(expr, stream)
    }

}