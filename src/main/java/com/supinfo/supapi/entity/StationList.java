package com.supinfo.supapi.entity;

import com.supinfo.supapi.entity.association.StationLineAssociation;

public class StationList {
	private Station start;
	
	private Station stop;
	
	private long line;
	
	StationList(StationList other){
		this.start = other.getStart();
		this.stop = other.getStop();
	}

	public StationList() {
	}

	public Station getStart() {
		return start;
	}

	public void setStart(Station start) {
		this.start = start;
	}

	public Station getStop() {
		return stop;
	}

	public void setStop(Station stop) {
		this.stop = stop;
	}

	public long getLine() {
		return line;
	}

	public void setLine(long line) {
		this.line = line;
	}

	public Line getCommonLine() {
		for(StationLineAssociation l : start.getLines()){
			long line_id = l.getLine().getId();
			for(StationLineAssociation l1 : stop.getLines()){
				if(line_id == l1.getLine().getId()){
					return l1.getLine();
				}
			}
		}
		return null;
	}
	
	
}
