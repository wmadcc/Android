package com.wmadcc.keep_accounts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

public class CustomTypesEditor {
	
	private Activity mActivity;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	private final static int[] 
			presetEarningTypesDrawable = {
				R.drawable.salary, 
				R.drawable.investment, 
				R.drawable.custom
	},
			presetExpenseTypesDrawable = {
				R.drawable.normal, 
				R.drawable.food, 
				R.drawable.shopping,
				R.drawable.dress, 
				R.drawable.traffic, 
				R.drawable.entertainment,
				R.drawable.social_contact, 
				R.drawable.living, 
				R.drawable.communication,
				R.drawable.custom
	};
	
	public final static String
		CUSTOM_CATEGORY_COLLECTION = "customCategory",
		CUSTOM_EARNING_TYPE_COUNT = "customEarningTypeCount",
		CUSTOM_EARNING_BASE_NAME = "customEarning",
		CUSTOM_EXPENSE_TYPE_COUNT = "customExpenseTypeCount",
		CUSTOM_EXPENSE_BASE_NAME = "custonExpense";
		
	public CustomTypesEditor(Activity acticity) {
		mActivity = acticity;
		sharedPreferences = mActivity.getSharedPreferences(
				CUSTOM_CATEGORY_COLLECTION, Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}
	
	public boolean isEarningTypeExist(String earningTypeName) {
		int earningTypeCount = sharedPreferences
				.getInt(CUSTOM_EARNING_TYPE_COUNT, 0);
		for (int i = 0; i < earningTypeCount; i++) {
			String earningType = 
					sharedPreferences.getString(CUSTOM_EARNING_BASE_NAME + i, "");
			if (earningType.equals(earningTypeName)) {
				return true;
			}
		}
		return false;
	}
	
	public void addEarningType(String earningTypeName) {
		int earningTypeCount = sharedPreferences
				.getInt(CUSTOM_EARNING_TYPE_COUNT, 0);
		
		editor.putString(CUSTOM_EARNING_BASE_NAME + earningTypeCount, earningTypeName);
		editor.putInt(CUSTOM_EARNING_TYPE_COUNT, earningTypeCount + 1);
		editor.commit(); 
	}
	
	public void editEarningType(String oldEarningType, String newEarningType) {
		if (isEarningTypeExist(newEarningType)) {
			return;
		}
		
		int earningTypeCount = sharedPreferences
				.getInt(CUSTOM_EARNING_TYPE_COUNT, 0);
		int position;
		for (position = 0; position < earningTypeCount; position++) {
			String earningType = 
					sharedPreferences
					.getString(CUSTOM_EARNING_BASE_NAME + position, "");
			if (earningType.equals(oldEarningType)) {
				break;
			}
		}
		if (position >= earningTypeCount) {
			return;
		}
		editor.putString(CUSTOM_EARNING_BASE_NAME + position, newEarningType);
		editor.commit(); 		
	}
	
	public void removeEarningType(String earningTypeName) {
		int earningTypeCount = sharedPreferences
				.getInt(CUSTOM_EARNING_TYPE_COUNT, 0);
		if (earningTypeCount <= 0) {
			return;
		}
		
		ArrayList<String> customEarningTypes = new ArrayList<String>();
		for (int i = 0; i < earningTypeCount; i++) {
			String earningType = 
					sharedPreferences.getString(CUSTOM_EARNING_BASE_NAME + i, "");
			if (!"".equals(earningType) && 
					!earningType.equals(earningTypeName)) {
				customEarningTypes.add(earningType);
			}
		}
		
		editor.clear().commit();
		
		for (int i = 0; i < customEarningTypes.size(); i++) {
			editor.putString(CUSTOM_EARNING_BASE_NAME + i, customEarningTypes.get(i));
		}
		
		editor.putInt(CUSTOM_EARNING_TYPE_COUNT, customEarningTypes.size());
		editor.commit(); 
	}
	
	public ArrayList<String> getCustomEarningTypes() {
		int earningTypeCount = sharedPreferences
				.getInt(CUSTOM_EARNING_TYPE_COUNT, 0);
		
		ArrayList<String> customEarningTypes = new ArrayList<String>();
		for (int i = earningTypeCount - 1; i >= 0 ; i--) {
			String earningType = 
					sharedPreferences.getString(CUSTOM_EARNING_BASE_NAME + i, "");
			if (!"".equals(earningType)) {
				customEarningTypes.add(earningType);
			}
		}
		return customEarningTypes;
	}
	
	public boolean isExpenseTypeExist(String expenseTypeName) {
		int expenseTypeCount = sharedPreferences
				.getInt(CUSTOM_EXPENSE_TYPE_COUNT, 0);
		for (int i = 0; i < expenseTypeCount; i++) {
			String expenseType = 
					sharedPreferences.getString(CUSTOM_EXPENSE_BASE_NAME + i, "");
			if (expenseType.equals(expenseTypeName)) {
				return true;
			}
		}
		return false;
	}
	
	public void addExpenseType(String expenseTypeName) {
		int expenseTypeCount = sharedPreferences
				.getInt(CUSTOM_EXPENSE_TYPE_COUNT, 0);
		
		editor.putString(CUSTOM_EXPENSE_BASE_NAME + expenseTypeCount, expenseTypeName);
		editor.putInt(CUSTOM_EXPENSE_TYPE_COUNT, expenseTypeCount + 1);
		editor.commit(); 
	}
	
	public void editExpenseType(String oldExpenseType, String newExpenseType) {
		if (isExpenseTypeExist(newExpenseType)) {
			return;
		}
		
		int expenseTypeCount = sharedPreferences
				.getInt(CUSTOM_EXPENSE_TYPE_COUNT, 0);
		int position;
		for (position = 0; position < expenseTypeCount; position++) {
			String expenseType = 
					sharedPreferences
					.getString(CUSTOM_EXPENSE_BASE_NAME + position, "");
			if (expenseType.equals(oldExpenseType)) {
				break;
			}
		}
		if (position >= expenseTypeCount) {
			return;
		}
		editor.putString(CUSTOM_EXPENSE_BASE_NAME + position, newExpenseType);
		editor.commit(); 		
	}
	
	public void removeExpenseType(String expenseTypeName) {
		int expenseTypeCount = sharedPreferences
				.getInt(CUSTOM_EXPENSE_TYPE_COUNT, 0);
		if (expenseTypeCount <= 0) {
			return;
		}
		
		ArrayList<String> customExpenseTypes = new ArrayList<String>();
		for (int i = 0; i < expenseTypeCount; i++) {
			String expenseType = 
					sharedPreferences.getString(CUSTOM_EXPENSE_BASE_NAME + i, "");
			if (!"".equals(expenseType) && 
					!expenseType.equals(expenseTypeName)) {
				customExpenseTypes.add(expenseType);
			}
		}
		
		editor.clear().commit();
		
		for (int i = 0; i < customExpenseTypes.size(); i++) {
			editor.putString(CUSTOM_EXPENSE_BASE_NAME + i, customExpenseTypes.get(i));
		}
		
		editor.putInt(CUSTOM_EXPENSE_TYPE_COUNT, customExpenseTypes.size());
		editor.commit(); 
	}
	
	public ArrayList<String> getCustomExpenseTypes() {
		int expenseTypeCount = sharedPreferences
				.getInt(CUSTOM_EXPENSE_TYPE_COUNT, 0);
		
		ArrayList<String> customExpenseTypes = new ArrayList<String>();
		for (int i = expenseTypeCount - 1 ; i >= 0; i--) {
			String expenseType = 
					sharedPreferences.getString(CUSTOM_EXPENSE_BASE_NAME + i, "");
			if (!"".equals(expenseType)) {
				customExpenseTypes.add(expenseType);
			}
		}
		return customExpenseTypes;
	}
	
	public Drawable getEarningCategoryDrawable(String categoryName) {
		String[] earningTypesNames = mActivity.getResources()
				.getStringArray(R.array.preset_earning_types);
		int categoryPos;
		for (categoryPos = 0; 
				categoryPos < earningTypesNames.length; categoryPos++) {
			if (earningTypesNames[categoryPos].equals(categoryName)) {
				return mActivity.getResources()
						.getDrawable(presetEarningTypesDrawable[categoryPos]);
			}
		}
		return mActivity.getResources()
				.getDrawable(presetEarningTypesDrawable[categoryPos]);
	}
	
	public Drawable getExpenseCategoryDrawable(String categoryName) {
		String[] expenseTypesNames = mActivity.getResources()
				.getStringArray(R.array.preset_expense_types);
		int categoryPos;
		for (categoryPos = 0; 
				categoryPos < expenseTypesNames.length; categoryPos++) {
			if (expenseTypesNames[categoryPos].equals(categoryName)) {
				return mActivity.getResources()
						.getDrawable(presetExpenseTypesDrawable[categoryPos]);
			}
		}
		return mActivity.getResources()
				.getDrawable(presetExpenseTypesDrawable[categoryPos]);
	}
	
	public Drawable getCategoryDrawabe(boolean isEarning, String categoryName) {
		if (categoryName.equals(StaticSettings.ADD_CATEGORY_NAME)) {
			return mActivity.getResources()
					.getDrawable(R.drawable.add_category);
		}
		if (isEarning) {
			return getEarningCategoryDrawable(categoryName);
		} else {
			return getExpenseCategoryDrawable(categoryName);
		}
	}
	
}
