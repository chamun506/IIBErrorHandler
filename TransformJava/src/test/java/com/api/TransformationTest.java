/**
 * 
 */
package com.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import junit.framework.TestCase;

/**
 * @author sanketsw
 *
 */
public class TransformationTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testTransformRequestData() throws JSONException {
		String in  = "{  \"left\": 0,  \"right\": 0 }";
		String expected = "{  \"right\": 0, \"left\": 100 }";
		String notExpected = "{  \"left\": 0,  \"right\": 0 }";
		
		String out =  new Transformation().transformRequestData(in);
		
		JSONObject json = new JSONObject(out);
		JSONAssert.assertEquals(expected, json, false);
		JSONAssert.assertNotEquals(notExpected, json, false);
	}
	
	@Test
	public void testTransformResponseData() throws JSONException {
		String in  = "{\"imeplementation\":\"Java_SpringBoot\",\"result\":\"35\"}";
		String expected = "{\"imeplementation\":\"IIB REST API\"}";
		String notExpected = "{\"imeplementation\":\"Java_SpringBoot\"}";
		
		String out =  new Transformation().transformResponseData(in);
		
		JSONObject json = new JSONObject(out);
		JSONAssert.assertEquals(expected, json, false);
		JSONAssert.assertNotEquals(notExpected, json, false);
	}

}
