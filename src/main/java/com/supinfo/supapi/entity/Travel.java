package com.supinfo.supapi.entity;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Travel {
	private List<SearchStep> aller;
	
	private List<SearchStep> retour;

	public List<SearchStep> getAller() {
		return aller;
	}

	public void setAller(List<SearchStep> aller) {
		this.aller = aller;
	}

	public List<SearchStep> getRetour() {
		return retour;
	}

	public void setRetour(List<SearchStep> retour) {
		this.retour = retour;
	}
}
