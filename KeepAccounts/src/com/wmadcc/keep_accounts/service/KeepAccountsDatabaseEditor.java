package com.wmadcc.keep_accounts.service;

import java.util.ArrayList;

import com.wmadcc.keep_accounts.AccountsItem;
import com.wmadcc.keep_accounts.StaticSettings;
import com.wmadcc.keep_accounts.provider.KeepAccountsDatabaseHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class KeepAccountsDatabaseEditor {
	
	KeepAccountsDatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDatebase;
	
	public KeepAccountsDatabaseEditor(
			KeepAccountsDatabaseHelper datebaseHelper) {
		mDatabaseHelper = datebaseHelper;
	}
	
	public boolean insertOrUpdateAccountsItem(
			AccountsItem accountsItem) {
		try {
			
			if (!isTableExists(StaticSettings
					.DEFAULT_TABLE_NAME)) {
				System.out.println("Create new table");
				createNewTable(StaticSettings
						.DEFAULT_TABLE_NAME); 
			}
			
			if (isItemExists(accountsItem))
				UpdateAccountsItem(accountsItem);
			else
				insertAccountsItem(accountsItem);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
			
		return true;
	}
		
	public ArrayList<AccountsItem> getAccountsItems(
			String sql, String[] selectionArgs) {
		openDatebase();
		Cursor cursor = mDatebase
				.rawQuery(sql, selectionArgs);
		ArrayList<AccountsItem> accountsItemsList = 
				convertCursorToItemsList(cursor);
		closeDatebase();
		return accountsItemsList;
	}
	
	public boolean deleteAccountsItem(
			AccountsItem accountsItem) {
		try {			
			if (isItemExists(accountsItem))
				deleteAccountsItem(accountsItem.getId());
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}	
		return true;
	}
	
	private void openDatebase() {
		mDatebase = mDatabaseHelper
				.getWritableDatabase();
	}
	
	private void closeDatebase() {
		mDatebase.close();
	}
	
	private boolean isTableExists(String tableName) {
		if (tableName == null ||
				tableName.equals("")) 
			return false;
		
		boolean result = false;
		
		openDatebase();
        Cursor cursor = null;
        
        try {
            String sql = "select count(*) as c from Sqlite_master "
            		+ "where type ='table' and name ='" 
            		+ tableName.trim() + "' ";
            cursor = mDatebase.rawQuery(sql, null);
            
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if(count>0){
                        result = true;
                }
            }  
        } catch (Exception ex) {
        	closeDatebase();
            ex.printStackTrace();
        }     
        return result;
	}
	
	private void createNewTable(String tableName) {
		final String createTableSQL = 
				"create table if not exists " 
				+ tableName + " ("
				+ StaticSettings.ID + " "
				+ "integer primary key autoincreament, "
				+ StaticSettings.YEAR + ", "
				+ StaticSettings.MONTH + ", "
				+ StaticSettings.DAY + ", "
				+ StaticSettings.IS_EARNING + ", "
				+ StaticSettings.CATEGORY + ", "
				+ StaticSettings.WAY_OF_PAYMENT + ", "
				+ StaticSettings.VALUE + ", "
				+ StaticSettings.IS_MARKED + ", "
				+ StaticSettings.DETAIL + ")";
		
		openDatebase();
		mDatebase.execSQL(createTableSQL);
		closeDatebase();
	}
	
	private boolean isItemExists(
			AccountsItem accountsItem) {
		return false;
	}
		
	private ArrayList<AccountsItem> convertCursorToItemsList(
			Cursor cursor) {
		ArrayList<AccountsItem> accountsItemsList = 
				new ArrayList<AccountsItem>();
		int id, year, month, day;
		boolean isEarning, isMarked;
		float value;
		String category, wayOfPayment, detail;
		AccountsItem accountsItem;
		
		cursor.move(-1);
		while (cursor.moveToNext()) {
			id = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings.ID));
			year = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings.YEAR));
			month = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings.MONTH));
			day = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings.DAY));
			isEarning = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings
							.IS_EARNING)) == 0 ? 
									false : true;
			category = cursor.getString(
					cursor.getColumnIndex(
							StaticSettings.CATEGORY));
			wayOfPayment = cursor.getString(
					cursor.getColumnIndex(
							StaticSettings.WAY_OF_PAYMENT));
			value = cursor.getFloat(
					cursor.getColumnIndex(
							StaticSettings.VALUE));
			isMarked = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings
							.IS_MARKED)) == 0 ? 
									false : true;
			detail = cursor.getString(
					cursor.getColumnIndex(
							StaticSettings.DETAIL));
			accountsItem = 
					new AccountsItem(id, year, month, day, 
							isEarning, category, wayOfPayment,
							value, isMarked, detail);
			accountsItemsList.add(accountsItem);
		}
		return accountsItemsList;
	}
	
	private boolean insertAccountsItem(
			AccountsItem accountsItem) {
		openDatebase();
		closeDatebase();
		return false;
	}

	private boolean UpdateAccountsItem(
			AccountsItem accountsItem) {
		openDatebase();
		closeDatebase();
		return false;
	}
	
	private boolean deleteAccountsItem(
			int accountsItemId) {
		openDatebase();
		closeDatebase();
		return false;
	}
	
}
