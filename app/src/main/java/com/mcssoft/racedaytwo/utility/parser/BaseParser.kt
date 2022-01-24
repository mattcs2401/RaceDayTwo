package com.mcssoft.racedaytwo.utility.parser

import com.mcssoft.racedaytwo.utility.DataResult
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.InputStream
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

/**
 * Utility class that has the core parse method.
 */
open class BaseParser {

    /**
     * Generic method to parse using the given XPath expression.
     * @param xpathExpr: The XPath expression to parse on.
     * @param inStream: The input stream to parse on.
     * @return: An Array of Map<String,String> (Node LocalName and NodeValue).
     * @note: Will throw an exception if the given path cannot be evaluated, or on any other
     *        exception.
     */
    fun parse(xpathExpr: String, inStream: InputStream): DataResult<ArrayList<MutableMap<String, String>>> {
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
            val lArray = arrayListOf<MutableMap<String,String>>()
            return DataResult.Error(lArray)
        }
        finally {
            inStream.close()
        }
        return DataResult.Success(lMap)
    }

}