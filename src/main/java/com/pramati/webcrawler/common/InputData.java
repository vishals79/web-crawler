package com.pramati.webcrawler.common;

public class InputData {
	
	private int criteriaNo;
	private String downloadPath;
	private String baseUrl;
	private String homeAddress;
	
	public InputData(int criteriaNo, String downloadPath, String baseUrl,
			String homeAddress) {
		super();
		this.criteriaNo = criteriaNo;
		this.downloadPath = downloadPath;
		this.baseUrl = baseUrl;
		this.homeAddress = homeAddress;
	}
	
	public int getCriteriaNo() {
		return criteriaNo;
	}
	public void setCriteriaNo(int criteriaNo) {
		this.criteriaNo = criteriaNo;
	}
	public String getDownloadPath() {
		return downloadPath;
	}
	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
}
