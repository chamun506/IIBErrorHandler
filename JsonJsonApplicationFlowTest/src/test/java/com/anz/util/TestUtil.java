package com.anz.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.broker.config.common.CommsMessageConstants;
import com.ibm.broker.config.common.XMLHelper;
import com.ibm.broker.config.proxy.AttributeConstants;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.Checkpoint;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.RecordedTestData;


public class TestUtil extends TestCase {
	

	public void testOnServerUsingIntegrationAPI(String integrationNodeName, String integrationServerName, String inputMessageFile, 
			String applicationName, String flowName, String  injectNodeUUID,List<TestNode> testNodeList) throws Exception {
	

			// Integration API initialization
			BrokerProxy nodeProxy = BrokerProxy.getLocalInstance(integrationNodeName);
			if (!nodeProxy.isRunning())	fail("Node proxy is not running");
			
			//Trace
			System.out.println("nodeproxy status, running = " + nodeProxy.isRunning());
			
			ExecutionGroupProxy serverProxy = nodeProxy.getExecutionGroupByName(integrationServerName);
			try {
				if (!serverProxy.isRunning()) serverProxy.start();
				
				//Trace
				System.out.println("serverproxy status, running = " + serverProxy.isRunning());
				
				System.out.println("clear Recorded Test Data");
				serverProxy.clearRecordedTestData();
				serverProxy.setInjectionMode(AttributeConstants.MODE_DISABLED);
				serverProxy.setTestRecordMode(AttributeConstants.MODE_DISABLED);
				
				// ENABLE injection and recording mode
				serverProxy.setInjectionMode(AttributeConstants.MODE_ENABLED);
				serverProxy.setTestRecordMode(AttributeConstants.MODE_ENABLED);
				
				// the calls above are asynchronous. We could implement a listener to check when the
				// calls have taken effect but to keep the code simple we'll include a 1 second sleep.
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
				
				// INJECT a previously-recorded message to drive the flow				
				String file = inputMessageFile;
				
				//Trace
				System.out.println("input data file = " + inputMessageFile);
				InputStream stream = this.getClass().getResourceAsStream("../"+ inputMessageFile);
				if(stream == null) fail("Error reading input data file.");
				String message = IOUtils.toString(stream); 
				
				//Trace
				System.out.println("message to be injected= " + message);
				
				//SET properties
				Properties injectProps = new Properties();
				injectProps.setProperty(AttributeConstants.DATA_INJECTION_APPLICATION_LABEL, applicationName); 		
				injectProps.setProperty(AttributeConstants.DATA_INJECTION_MESSAGEFLOW_LABEL, flowName); 			
				injectProps.setProperty(AttributeConstants.DATA_INJECTION_NODE_UUID, injectNodeUUID);
				injectProps.setProperty(AttributeConstants.DATA_INJECTION_WAIT_TIME, "60000");
				injectProps.setProperty(AttributeConstants.DATA_INJECTION_MESSAGE_SECTION, message);
				
				boolean synchronous = true; // making the call synchronous means we don't need to sleep afterwards
				
				boolean result = serverProxy.injectTestData(injectProps, synchronous);
				
				//Trace
				System.out.println("Injection result = " + result);
				
				retreiveRecordedMessageAtEachTestNode(serverProxy , testNodeList);
			} catch(Exception e) {
				e.printStackTrace();
				fail(e.getMessage());				
			} finally {				
				//Clear recorded data and reset server to turn off recording and injection
				System.out.println("clear Recorded Test Data");
				serverProxy.clearRecordedTestData();
				serverProxy.setInjectionMode(AttributeConstants.MODE_DISABLED);
				serverProxy.setTestRecordMode(AttributeConstants.MODE_DISABLED);
			}
			
		}
	
	//RETRIEVE messages at each test node
	//TO DO: Try/catch
	static void retreiveRecordedMessageAtEachTestNode(ExecutionGroupProxy serverProxy ,List<TestNode> testNode) throws Exception {
	
		// RETRIEVE recorded message for each test node			
			
		for(TestNode node : testNode){			
			System.out.println("Validating data for testNodeUUID = " + node.getTestNodeUUID());
			
			Properties filterProps = new Properties();			
			filterProps.put(Checkpoint.PROPERTY_SOURCE_NODE_NAME, node.getTestNodeUUID());			
			List<RecordedTestData> dataList = serverProxy.getRecordedTestData(filterProps);
			
			//Check that at least one recorded message was retrieved
			if (dataList == null || dataList.size() == 0) { 				
				//Trace
				System.out.println("dataList is null");				
				fail("returned message is null");
			}
			
			//Trace
			System.out.println("dataList not null. Length=" + dataList.size());
			/*for(RecordedTestData data: dataList) {
				System.out.println(data.getTestData().getName());
				System.out.println(data.getTestData().getUuid());
				System.out.println(data.getTestData().getMessage());
			}*/
			
			// TEST the message against some criteria
			boolean usingREST = false;
			runAssertionsAgainstData(dataList, node.getxPathAssertionList(), usingREST);
				
			//Trace
			System.out.println("Assertions for" + node.getTestNodeUUID() + " are successful");
			
		}			
		
	}

	//Helper method to run assertions against the recorded message you retrieve - this is used for the REST example too
	static void runAssertionsAgainstData(List<RecordedTestData> dataList, List<String> xPathAssertionList, boolean useREST) throws Exception {
		
		//Trace
		System.out.println("Inside runAssertionsAgainstTestData. datalist length=" + dataList.size());		
		
		//TEST retrieved message
		for (RecordedTestData data : dataList) {
			String messageData = data.getTestData().getMessage();
			
			//Trace
			System.out.println("returned message[" + dataList.indexOf(data) + "] = " + messageData);
			
			ByteArrayInputStream bais = new ByteArrayInputStream(messageData.getBytes(CommsMessageConstants.TEXT_ENCODING));
			Document logicalTree = XMLHelper.parse(bais);
			Element logicalTreeRoot = logicalTree.getDocumentElement();
			
			//Evaluate each XPath expression against the parsed XML
			XPath xPath = XPathFactory.newInstance().newXPath();
			for(String xPathAssertion : xPathAssertionList){
				
				NodeList nodes = (NodeList)xPath.evaluate(xPathAssertion, logicalTreeRoot, XPathConstants.NODESET);
				
				//Trace
				System.out.println("xPathAssertion[" + xPathAssertionList.indexOf(xPathAssertion) + "] = " + xPathAssertion);
				assertTrue("ERROR: no elements found that match XPathAssertion[" + xPathAssertionList.indexOf(xPathAssertion) + "] = " + xPathAssertion, nodes.getLength() > 0);
					
			}
		}
		
	}
	

	
	//Main method
	public static void main(String[] args){
		
		//Trace
		System.out.println("Inside main");
			
		//USER INPUT DATA
		String integrationNodeName = "TESTNODE_root";
		String integrationServerName = "default";
		String applicationName = "JsonJsonApplication";
		String flowName = "ZOSRest01";
		//File name, containing data to inject
		String inputMessageFile = "inputData.xml";
		/*Injection node UUID:
		* 	<FlowName>#<NodeID>
		* 	NodeIDs can be found within the .msgflow file
		* Supported by the following nodes:
		*   MQ Input
		*   MQTT Subscribe
		*   SOAP Input
		*   HTTP Input
		*   File Input
		*/
		String injectNodeUUID = "ZOSRest01#FCMComposite_1_1";
		
		// ZOSRest01#FCMComposite_1_2 2
		// /message/JSON/Data/imeplementation[.='Java_SpringBoot']
		// /message/JSON/Data/result[.='3']
		
		
		//Create test nodes and corresponding assertions from user input
		int numberOfTestNodes = 1;
		int numberOfAssertions = 0;
		List<TestNode> testNodeList = new ArrayList<TestNode>();
		Scanner scanObj = new Scanner(System.in);
		for(int testNodeNumber = 0; testNodeNumber < numberOfTestNodes; testNodeNumber++){
			TestNode node = new TestNode();
			System.out.println("Enter test node UUID, followed by the number of xPathAssertions you want to test, separated by a space:");
			//scanObj.nextLine();
			
			//Read node UUID from input
			//<FlowName>#<NodeID>
			node.setTestNodeUUID(scanObj.next());
			numberOfAssertions = scanObj.nextInt();
			//Echo
			System.out.println("TestNodeUUID = " + node.getTestNodeUUID() + ", numberOdAssertions = " + numberOfAssertions);
			
			System.out.println("	Enter XPath assertions one per line:");
			scanObj.nextLine();
			for(int assertionNumber = 0; assertionNumber < numberOfAssertions; assertionNumber++){
				//System.out.println("	Enter next XPath assertion:");
				//scanObj.nextLine();
				
				//Read xPath assertions from console
				node.addxPathAssertion(scanObj.nextLine());
			}
			//Echo
			System.out.println("AssertionList = " + node.getxPathAssertionList());
			testNodeList.add(node);
		}
		
		//TEST FLOW
		try {
			new TestUtil().testOnServerUsingIntegrationAPI(integrationNodeName, integrationServerName, inputMessageFile, applicationName, flowName,  injectNodeUUID, testNodeList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
