package com.szu.twowayradio.domain;

public class PlayRequest {

	private CommonRequest cr;
	
	private int startOrEnd;
	
	private int streamType;

	public CommonRequest getCr() {
		return cr;
	}

	public void setCr(CommonRequest cr) {
		this.cr = cr;
	}

	public int getStartOrEnd() {
		return startOrEnd;
	}

	public void setStartOrEnd(int startOrEnd) {
		this.startOrEnd = startOrEnd;
	}

	public int getStreamType() {
		return streamType;
	}

	public void setStreamType(int streamType) {
		this.streamType = streamType;
	}
	
	
}
