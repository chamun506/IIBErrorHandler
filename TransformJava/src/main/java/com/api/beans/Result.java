package com.api.beans;

import java.io.Serializable;

public class Result implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3955074699838807226L;
	String imeplementation;
	String result;
	String comment;
	
	/**
	 * @return the imeplementation
	 */
	public String getImeplementation() {
		return imeplementation;
	}
	/**
	 * @param imeplementation the imeplementation to set
	 */
	public void setImeplementation(String imeplementation) {
		this.imeplementation = imeplementation;
	}
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	

	
	
	
	
	
}
