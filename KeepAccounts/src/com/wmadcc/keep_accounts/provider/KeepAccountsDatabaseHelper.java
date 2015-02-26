package com.wmadcc.keep_accounts.provider;

import com.wmadcc.keep_accounts.StaticSettings;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KeepAccountsDatabaseHelper extends SQLiteOpenHelper {
	
	public KeepAccountsDatabaseHelper(Context context, int version) {
		super(context, StaticSettings.DATEBASE_NAME, null, version);
	}

	public KeepAccountsDatabaseHelper(Context context, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, StaticSettings.DATEBASE_NAME,
				null, version, errorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		final String createTableSQL = 
					"create table if not exists " 
					+ StaticSettings.DEFAULT_TABLE_NAME + "("
					+ StaticSettings.ID + " "
					+ "integer primary key autoincrement, "
					+ StaticSettings.YEAR + " integer, "
					+ StaticSettings.MONTH + " integer, "
					+ StaticSettings.DAY + " integer, "
					+ StaticSettings.IS_EARNING + " integer, "
					+ StaticSettings.CATEGORY + " text, "
					+ StaticSettings.WAY_OF_PAYMENT + " text, "
					+ StaticSettings.VALUE + " text, "
					+ StaticSettings.IS_MARKED + " integer, "
					+ StaticSettings.DETAIL + " text)";
		
		db.execSQL(createTableSQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
