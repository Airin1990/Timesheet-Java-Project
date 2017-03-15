package org.weijie.wallethub.mytimesheet.model;

public class Share {

	private long sid;
	private long tid;
	private long uid;
	private int shareMode;
	private int shareStatus;
	
	public Share() {
		
	}

	public Share(long sid, long tid, long uid, int shareMode, int shareStatus) {
		super();
		this.sid = sid;
		this.tid = tid;
		this.uid = uid;
		this.shareMode = shareMode;
		this.shareStatus = shareStatus;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public long getTid() {
		return tid;
	}

	public void setTid(long tid) {
		this.tid = tid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
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
