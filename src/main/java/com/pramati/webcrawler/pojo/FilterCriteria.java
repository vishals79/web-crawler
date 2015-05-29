package com.pramati.webcrawler.pojo;

public class FilterCriteria {

	private String forYear;
	private String from;
	private String orgName;
	private Date beforeDate;
	private Date afterDate;
	private Date onDate;
	private BetweenDates betweenDates;
	private BetweenYears betweenYears;
	private String afterYear;
	private String beforeYear;

	public String getForYear() {
		return forYear;
	}

	public void setForYear(String forYear) {
		this.forYear = forYear;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public Date getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}

	public Date getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}

	public Date getOnDate() {
		return onDate;
	}

	public void setOnDate(Date onDate) {
		this.onDate = onDate;
	}

	public BetweenDates getBetweenDates() {
		return betweenDates;
	}

	public void setBetweenDates(BetweenDates betweenDates) {
		this.betweenDates = betweenDates;
	}

	public BetweenYears getBetweenYears() {
		return betweenYears;
	}

	public void setBetweenYears(BetweenYears betweenYears) {
		this.betweenYears = betweenYears;
	}

	public String getAfterYear() {
		return afterYear;
	}

	public void setAfterYear(String afterYear) {
		this.afterYear = afterYear;
	}

	public String getBeforeYear() {
		return beforeYear;
	}

	public void setBeforeYear(String beforeYear) {
		this.beforeYear = beforeYear;
	}
}
