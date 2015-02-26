package com.wmadcc.keep_accounts;

public class CategoryItemWrapper {
	
	private String category;
	private float ratioOfAll;
	private Double sumValue;
	private boolean isEarning;
	
	public CategoryItemWrapper(String category, 
			Double sumValue, float ratioOfAll, boolean isEarning) {
		this.category = category;
		this.sumValue = sumValue;
		this.ratioOfAll = ratioOfAll;
		this.isEarning = isEarning;
	}
	
	public String getCategory() {
		return category;
	}
	
	public float getRatio() {
		return ratioOfAll;
	}
	
	public double getValue() {
		return sumValue;
	}
	
	public boolean isEarning() {
		return isEarning;
	}
}

