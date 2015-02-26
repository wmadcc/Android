package com.wmadcc.keep_accounts;

import java.io.Serializable;

public class AccountsItem implements 
		Serializable {

	private static final long serialVersionUID = 7776L;
	
	private int id, year, month, day;
	private boolean isEarning, isMarked;
	private double value;
	private String category, wayOfPayment, detail;
	
	public AccountsItem(
			int id, 
			int year,
			int month, 
			int day, 
			boolean isEarning,
			String category, 
			String wayOfPayment,
			double value, 
			boolean isMarked,
			String detail) {
		this.id = id;
		this.year = year;
		this.month = month;
		this.day = day;
		this.isEarning = isEarning;
		this.category = category;
		this.wayOfPayment = wayOfPayment;
		this.value = value;
		this.isMarked = isMarked;
		this.detail = detail;
	}
	
	public AccountsItem(
			int year,
			int month, 
			int day, 
			boolean isEarning,
			String category, 
			String wayOfPayment,
			double value, 
			boolean isMarked,
			String detail) {
		this(-1, year, month, day, isEarning, category, 
				wayOfPayment, value, isMarked, detail);
	}
	
	public int getId() {
		return id;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getDay() {
		return day;
	}
	
	public boolean getIsEarning() {
		return isEarning;
	}
	
	public boolean getIsMarked() {
		return isMarked;
	}
	
	public double getValue() {
		return value;
	}
	
	public String getCategory() {
		return category;
	}
		
	public String getWayOfPayment() {
		return wayOfPayment;
	}
	
	public String getDetail() {
		return detail;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	public void setIsEarning(boolean isEarning) {
		this.isEarning = isEarning;
	}
	
	public void setIsMarked(boolean isMarked) {
		this.isMarked = isMarked;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setWayOfPayment(String wayOfPayment) {
		this.wayOfPayment = wayOfPayment;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(StaticSettings
				.ID  + "=" + getId() + ", ");
		stringBuffer.append(StaticSettings
				.YEAR  + "=" + getYear() + ", ");
		stringBuffer.append(StaticSettings
				.MONTH  + "=" + getMonth() + ", ");
		stringBuffer.append(StaticSettings
				.DAY  + "=" + getDay() + ", ");
		stringBuffer.append(StaticSettings
				.IS_EARNING  + "=" + getIsEarning() + ", ");
		stringBuffer.append(StaticSettings
				.WAY_OF_PAYMENT  + "=" + getWayOfPayment() + ", ");
		stringBuffer.append(StaticSettings
				.CATEGORY  + "=" + getCategory() + ", ");
		stringBuffer.append(StaticSettings
				.VALUE  + "=" + getValue() + ", ");
		stringBuffer.append(StaticSettings
				.DETAIL  + "=" + getDetail() + ", ");
		stringBuffer.append(StaticSettings
				.IS_MARKED  + "=" + getIsMarked());
		return stringBuffer.toString();
	}
	
	@Override
	public boolean equals(Object cmpareObject) {
		if (cmpareObject == this) {
			return true;
		}
		
		if (cmpareObject == null) {
			return false;
		}
		
        if (!(cmpareObject instanceof AccountsItem)) {
            return false;
        }
        
        AccountsItem accountsItem = (AccountsItem) cmpareObject;	
    	if (accountsItem.getId() != id)
    		return false;
    	if (accountsItem.getYear() != year)
    		return false;
    	if (accountsItem.getMonth() != month)
    		return false;
    	if (accountsItem.getDay() != day)
    		return false;
    	if (accountsItem.getIsEarning() != isEarning)
    		return false;
    	if (accountsItem.getIsMarked() != isMarked)
    		return false;
    	if (category != null) {
    		if (!category.equals(accountsItem.getCategory())) {
    			return false;
    		}
    	} else if (accountsItem.getCategory() != null) {
    		return false;	
    	}
    	if (wayOfPayment != null) {
    		if (!wayOfPayment.equals(accountsItem.getWayOfPayment())) {
    			return false;
    		}
    	} else if (accountsItem.getWayOfPayment() != null) {
    		return false;	
    	}
    	if (detail != null) {
    		if (!detail.equals(accountsItem.getDetail())) {
    			return false;
    		}
    	} else if (accountsItem.getDetail() != null) {
    		return false;	
    	}
    	    	
    	if (Math.abs(accountsItem.getValue() - value) >= 0.0001f)
        	return false;
    	
    	return true;
	} 
	
}
