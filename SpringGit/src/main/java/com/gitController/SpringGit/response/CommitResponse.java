package com.gitController.SpringGit.response;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;


public class CommitResponse implements Serializable{

	public CommitResponse() {
		
		
	}
	
	private String commitId;
	private String name;
	private String time;
	private String message;
	
	
	public CommitResponse(ObjectId commitId, String name, int commitTime , String message) {		
		

		String date = Integer.toString(commitTime);
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss:SSS Z");
		try {
			Date expiry = new Date(Long.parseLong(date) * 1000);
			
			this.time = df.format(expiry);
		 } catch (Exception ex) {
		  ex.getStackTrace();
		}
		
	    this.commitId = commitId.toString();
	    this.name = name;
		
	    
		this.message = message;			
		
	}


	public String getCommitId() {
		return commitId;
	}


	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
	
}
