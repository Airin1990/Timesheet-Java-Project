package org.weijie.wallethub.mytimesheet.model;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Timesheet {

	private long tid;
	private String tName;
	private long uid;
	private Timestamp createDate;
	private Timestamp updateDate;
	private int shareMode;
	private int shareStatus;
	private String firstName;
	private String lastName;

	public Timesheet() {

	}
	
	public Timesheet(long tid, String tName, long uid, Timestamp createDate, Timestamp updateDate) {
		super();
		this.tid = tid;
		this.tName = tName;
		this.uid = uid;
		this.createDate = createDate;
		this.updateDate = updateDate;
		// -1 means that timesheet is owned by this user
		this.shareMode = -1;
		this.shareStatus = -1;
	}


	public Timesheet(long tid, String tName, long uid, Timestamp createDate, Timestamp updateDate, String firstName, String lastName, int shareMode, int shareStatus) {
		super();
		this.tid = tid;
		this.tName = tName;
		this.uid = uid;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.shareMode = shareMode;
		this.shareStatus = shareStatus;
		this.firstName = firstName;
		this.lastName = lastName;	
	}
	public long getTid() {
		return tid;
	}
	public void setTid(long tid) {
		this.tid = tid;
	}
	public String gettName() {
		return tName;
	}
	public void settName(String tName) {
		this.tName = tName;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
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


}
