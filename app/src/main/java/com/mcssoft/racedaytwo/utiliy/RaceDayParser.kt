package com.mcssoft.racedaytwo.utiliy

import android.util.Log
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Parse the xml within the downloaded RaceDay.xml file.
 */
class RaceDayParser() {
    // The input stream. Basically the Xml based file contents that will be parsed.
    private var inStream: InputStream? = null

    // Secondary constructor TBA.
    constructor(iStream: InputStream) : this() {
        inStream = iStream
    }

    /**
     * Set the input stream value used by the XPath InputSource.
     * @param stream: The input stream to use.
     */    fun setInputStream(stream: InputStream) {
        inStream = stream
    }

    /**
     * Parse for all meetings.
     * @return A List<Map<LocalName, NodeValue>>.
     */
    fun parseForMeeting(): ArrayList<MutableMap<String, String>> {
        val expr = "Racing/RaceDay/Meetings/Meeting"
        return parse(expr)
    }

    /**
     * Parse for a specific <Race></Race> with a <Meeting></Meeting>.
     * @param meetingCode: The Meeting code, e.g. BR.
     * @return A Map<LocalName, NodeValue>.
     */
    fun parseForMeeting(meetingCode: String): MutableMap<String,String> {
        val expr = "/RaceDay/Meeting[@MeetingCode='$meetingCode']"
        return parse(expr)[0]     // only one Meeting is expected.
    }

    /**
     * Generic parse method. Will parse the given XPath expression into an ArrayList of Map.
     * @param xpathExpr: The XPath expression to parse on.
     * @return: An Array of Map<String,String> (Node LocalName and NodeValue).
     */
    private fun parse(xpathExpr: String): ArrayList<MutableMap<String, String>> {
        // TODO - a parse issue might not throw an exception, but lMap.size could be 0.
        val lMap = ArrayList<MutableMap<String, String>>()

        try {
            val xpath = XPathFactory.newInstance().newXPath()
            val lNodes = xpath.evaluate(xpathExpr, InputSource(inStream), XPathConstants.NODESET) as NodeList
            val mapGet = mutableMapOf<String, String>()

            if (lNodes.length > 0) {
                val len = lNodes.length
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
            }
        } catch(ex: Exception) {
            Log.e("TAG", "[RaceDayParser.parse] Exception: ${ex.message}")
        } finally {
            inStream?.close()
        }
        return lMap
    }

}
