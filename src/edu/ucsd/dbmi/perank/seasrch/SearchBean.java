package edu.ucsd.dbmi.perank.seasrch;

import java.util.ArrayList;

public class SearchBean {
	private String Link;
	private String preference;
	public ArrayList<String> name;
	public ArrayList<String> getName() {
		return name;
	}
	public void setName(ArrayList<String> name) {
		this.name = name;
	}
	public String getPreference() {
		return preference;
	}
	public void setPreference(String preference) {
		this.preference = preference;
	}
	public String getLink() {
		return Link;
	}
	public void setLink(String link) {
		Link = link;
	}
	public String getOutLink() {
		return outLink;
	}
	public void setOutLink(String outLink) {
		this.outLink = outLink;
	}
	public Double getLocalizedFingerPrint() {
		return LocalizedFingerPrint;
	}
	public void setLocalizedFingerPrint(Double localizedFingerPrint) {
		LocalizedFingerPrint = localizedFingerPrint;
	}
	private String outLink;
	private Double LocalizedFingerPrint;
	
}
