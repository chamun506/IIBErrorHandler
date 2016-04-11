package com.anz.util;

import java.util.ArrayList;
import java.util.List;

public class TestNode {
	
	private String testNodeUUID;
	
	private List<String> xPathAssertionList = new ArrayList<String>();
	
	
	
	/**
	 * Default constructor
	 */
	public TestNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param testNodeUUID
	 */
	public TestNode(String testNodeUUID) {
		super();
		this.testNodeUUID = testNodeUUID;
	}
	
	/**
	 * Add to existing assertions
	 * @param xPathAssertion
	 */
	public void addxPathAssertion(String xPathAssertion){
		xPathAssertionList.add(xPathAssertion);
	}

	public void setTestNodeUUID(String nodeUUID){
		testNodeUUID = nodeUUID;
	}
	
	public void setxPathAssertionList(List<String> xPathAssertionList) {
		this.xPathAssertionList = xPathAssertionList;
	}

	public String getTestNodeUUID(){
		return testNodeUUID;
	}
	
	public List<String> getxPathAssertionList(){
		return xPathAssertionList;
	}
}
