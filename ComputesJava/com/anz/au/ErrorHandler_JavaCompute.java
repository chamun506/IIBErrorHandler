package com.anz.au;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class ErrorHandler_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
		
		
			MbElement outRoot = outMessage.getRootElement();
			StringBuffer buffer = new StringBuffer();			
			String head = "<html><body text=\"#ff0000\"><form action=\"/TransferRequestForm\" method=post>";			
			buffer.append(head);
			
			MbElement httpResponseHeader = inAssembly.getMessage().getRootElement().getFirstElementByPath("HTTPResponseHeader");
			MbElement status = httpResponseHeader.getFirstElementByPath("X-Original-HTTP-Status-Line");
			String text = status.getValue().toString();
			//if (text.length() > 0) {
			  buffer.append("<h1>");					
			  buffer.append(text);
			  buffer.append("</h1>");				
			//}
								
			//}
			
			//Complete the HTML body
			String tail = "<br><input type=\"submit\" name=\"OK\" value=\"OK\"></form></body></html>";
			buffer.append(tail);
			
			
			//Create the Broker Blob Parser element
			MbElement outParser = outRoot.createElementAsLastChild(MbBLOB.PARSER_NAME);
			//Create the BLOB element in the Blob parser domain with the required text
			//MbElement outBody = outParser.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", buffer.toString().getBytes());
			outParser.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", buffer.toString().getBytes());
	
			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			//throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			//throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			//throw new MbUserException(this, "evaluate()", "", "", e.toString(),null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
