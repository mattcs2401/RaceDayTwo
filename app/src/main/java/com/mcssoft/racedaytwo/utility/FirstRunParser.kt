package com.mcssoft.racedaytwo.utility

import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.R
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class FirstRunParser(private val context: Context, fName: String) {

    private var inStream: InputStream? = null

    init {
        inStream = context.resources.assets.open(fName)
    }

    fun parseForFirstRun(): DataResult<ArrayList<MutableMap<String, String>>> {
        val expr = context.resources.getString(R.string.country_path)
        return parse(expr)
    }

    fun closeStream() {
        inStream?.close()
    }

    /**
     * Generic method to parse using the given XPath expression.
     * @param xpathExpr: The XPath expression to parse on.
     * @return: An Array of Map<String,String> (Node LocalName and NodeValue).
     * @note: Will throw an exception if the given path cannot be evaluated, or on any other
     *        exception.
     */
    private fun parse(xpathExpr: String): DataResult<ArrayList<MutableMap<String, String>>> {
        val lMap = ArrayList<MutableMap<String, String>>()

        try {
            val xpath = XPathFactory.newInstance().newXPath()
            val xPathExpression = xpath.compile(xpathExpr)
            val lNodes = xPathExpression.evaluate(InputSource(inStream), XPathConstants.NODESET) as NodeList
            val len = lNodes.length

            val mapGet = mutableMapOf<String, String>()
            for (ndx in 0..len) {
                val node = lNodes.item(ndx)
                if (node != null) {
                    val lNodeAttrs = node.attributes
                    for (ndx2 in 0 until lNodeAttrs.length) {
                        val attrNode = lNodeAttrs.item(ndx2)
                        mapGet[attrNode.localName] = attrNode.nodeValue
                    }
                    val mapPut = HashMap(mapGet)
                    lMap.add(mapPut)
                    mapGet.clear()
                }
            }
        } catch(ex: Exception) {
            Log.e("TAG", "[RaceDayParser.parse] Exception: Map size: ${lMap.size} Message: ${ex.message}")
            val lArray = arrayListOf<MutableMap<String,String>>()
            lArray.add(mutableMapOf("[RaceDayParser.parse] Exception: " to "${ex.message}"))
            return DataResult.Error(lArray)
        }
        finally {
            closeStream()
        }
        return DataResult.Success(lMap)
    }
}