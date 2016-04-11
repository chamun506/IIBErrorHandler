package com.anz;

import java.io.UnsupportedEncodingException;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;

public class Util {

	/** 
	 * Converts JSON string to a message tree and assign to outMessage
	 * @param outMessage outMessage
	 * @param outputJson JSON string
	 * @throws Exception
	 */
	public static void replaceJsonData(MbMessage outMessage, String outputJson) throws Exception {
		MbElement outMsgRootEl = outMessage.getRootElement();			    
	    String parserName = MbJSON.PARSER_NAME;
	    String messageType = "";
	    String messageSet = "";
	    String messageFormat = "";
	    int encoding = 0;
	    int ccsid = 0;
	    int options = 0; 
	    outMsgRootEl.getFirstElementByPath("JSON").detach();
	    outMsgRootEl.createElementAsLastChildFromBitstream(outputJson.getBytes("UTF-8"),
	    		   parserName, messageType, messageSet, messageFormat, encoding, ccsid,
	    		   options); 
		
	}

	/**
	 * Get the JSON Data from the message
	 * @param inMessage Message
	 * @return JSON String
	 * @throws Exception
	 */
	public static String getJsonData(MbMessage inMessage) throws Exception {
		byte[] bs = inMessage.getRootElement().getFirstElementByPath("JSON/Data").toBitstream(null, null, null, 0, 1208, 0);
	    String inputJson = new String(bs, "UTF-8");
	    return inputJson;
	}
	

}
