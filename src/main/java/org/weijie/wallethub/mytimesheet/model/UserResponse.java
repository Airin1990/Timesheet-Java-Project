package org.weijie.wallethub.mytimesheet.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserResponse extends User {

	private long tid;
	private int shareMode;
	private int shareStatus;
	
	public UserResponse() {
		super();
	}
	public UserResponse(long uid, String firstName, String lastName, String email, long fbid, Date created, long tid, int shareMode, int shareStatus) {
		super(uid, firstName, lastName, email, fbid, created);
		this.tid = tid;
		this.shareMode = shareMode;
		this.shareStatus = shareStatus;
	}
	public int getShareMode() {
		return shareMode;
	}
	public void setShareMode(int shareMode) {
		this.shareMode = shareMode;
	}
	public int getShareStatus() {
		return shareStatus;
	}
	public void setShareStatus(int shareStatus) {
		this.shareStatus = shareStatus;
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	
}
