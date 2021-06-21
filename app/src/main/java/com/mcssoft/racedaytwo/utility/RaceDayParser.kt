package com.mcssoft.racedaytwo.utility

import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.R
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * A utility class to parse the xml within the downloaded RaceDay.xml file.
 * @param context For access to string resources.
 */
class RaceDayParser(private val context: Context) {
    // The input stream. Basically the Xml based file contents that will be parsed.
    private var inStream: InputStream? = null

    // Secondary constructor TBA.
    constructor(context: Context, inStream: InputStream) : this(context) {
        this.inStream = inStream
    }

    constructor(context: Context, fileId: Long): this(context) {
        inStream = RaceDownloadManager(context).getFile(fileId)
    }

    /**
     * Set the input stream value used by the XPath InputSource.
     * @param inStream: The input stream to use.
     */    fun setInputStream(inStream: InputStream) {
        this.inStream = inStream
    }

    /**
     * Set the current input stream based upon a file id.
     * @param fileId: The file id.
     */
    fun setInputStream(fileId: Long) {
        inStream = RaceDownloadManager(context).getFile(fileId)
    }

    /**
     * Parse for all meetings.
     * @return A List<Map<LocalName, NodeValue>>.
     */
    fun parseForMeeting(): ArrayList<MutableMap<String, String>> {
        val expr = context.resources.getString(R.string.meeting_parse_path)
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
     * Generic method to parse using the given XPath expression.
     * @param xpathExpr: The XPath expression to parse on.
     * @return: An Array of Map<String,String> (Node LocalName and NodeValue).
     * @note: Will throw an exception if the given path cannot be evaluated, or on any other
     *        exception.
     */
    private fun parse(xpathExpr: String): ArrayList<MutableMap<String, String>> {
        val lMap = ArrayList<MutableMap<String, String>>()

        try {
            val xpath = XPathFactory.newInstance().newXPath()
            val lNodes = xpath.evaluate(xpathExpr, InputSource(inStream), XPathConstants.NODESET) as NodeList
            val len = lNodes.length

            if (len > 0) {
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
            } else {
                throw Exception("Could not evaluate XPath expression: ${xpathExpr}")
            }
        } catch(ex: Exception) {
            Log.e("TAG", "[RaceDayParser.parse] Exception: ${ex.message}")
        } finally {
            inStream?.close()
        }
        return lMap
    }

}
