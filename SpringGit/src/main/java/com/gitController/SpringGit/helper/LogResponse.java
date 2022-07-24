package com.gitController.SpringGit.helper;

import java.io.Serializable;
import java.util.List;

import com.gitController.SpringGit.response.CommitResponse;



public class LogResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CommitResponse> responseList;

	public LogResponse() {		

	}

	public LogResponse(List<CommitResponse> responseList) {
		super();
		this.responseList = responseList;
	}

	public List<CommitResponse> getResponseList() {
		return responseList;
	}

	public void setResponseList(List<CommitResponse> responseList) {
		this.responseList = responseList;
	}
}
