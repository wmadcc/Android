package com.wmadcc.keep_accounts.provider;

import com.wmadcc.keep_accounts.StaticSettings;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class KeepAccountsProvider extends ContentProvider {

	private KeepAccountsDatabaseHelper dbOpenHelper;
	private static final int ITEMS = 1;
	private static final int SINGLE_ITEM = 2;
	private static UriMatcher matcher = 
			new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		matcher.addURI(StaticSettings.AUTHORITY, 
				StaticSettings.ALL_ITEMS, ITEMS);
		matcher.addURI(StaticSettings.AUTHORITY,
				StaticSettings.SINGLE_ITEM + "/#", SINGLE_ITEM);		
	}
	
	public KeepAccountsProvider() {
		
	}
	
	@Override
	public boolean onCreate() {
		dbOpenHelper =
				new KeepAccountsDatabaseHelper(
						this.getContext(), 1);
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case ITEMS:
			return "vnd.android.cursor.dir/com.wmadcc.keep_accounts";
		case SINGLE_ITEM:
			return "vnd.android.cursor.item/com.wmadcc.keep_accounts";
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		switch (matcher.match(uri)) {
		case ITEMS:			
			if (sortOrder == null || "".equals(sortOrder)) 
				sortOrder = StaticSettings.DAY + " desc"; 
						
			return db.query(StaticSettings.DEFAULT_TABLE_NAME, projection, 
					selection, selectionArgs, null, null, sortOrder);
			
		case SINGLE_ITEM:
			long id = ContentUris.parseId(uri);
			String whereClause = StaticSettings.ID + "=" + id;
			if (selection != null && !"".equals(selection)) 
				whereClause = whereClause + " and " + selection;
			if (sortOrder == null || "".equals(sortOrder)) 
				sortOrder = StaticSettings.DAY + " desc";
			return db.query(StaticSettings.DEFAULT_TABLE_NAME, projection, 
					whereClause, selectionArgs, null, null, sortOrder);
			
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		switch (matcher.match(uri)) {
		case ITEMS:
			long rowId = db.insert(StaticSettings.DEFAULT_TABLE_NAME,
					StaticSettings.DETAIL, values);
			if (rowId > 0) {
				Uri itemUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(itemUri, null);
				return itemUri;
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		int num = 0;
		switch (matcher.match(uri)) {
		case ITEMS:
			num = db.delete(StaticSettings.DEFAULT_TABLE_NAME,
					selection, selectionArgs);
			break;
			
		case SINGLE_ITEM:
			long id = ContentUris.parseId(uri);
			String whereClause = StaticSettings.ID + "=" + id;
			if (selection != null && !"".equals(selection)) 
				whereClause = whereClause + " and " + selection;
			num = db.delete(StaticSettings.DEFAULT_TABLE_NAME,
					whereClause, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		int num = 0;
		switch (matcher.match(uri)) {
		case ITEMS:
			num = db.update(StaticSettings.DEFAULT_TABLE_NAME, 
					values, selection, selectionArgs);
			break;
			
		case SINGLE_ITEM:
			long id = ContentUris.parseId(uri);
			String whereClause = StaticSettings.ID + "=" + id;
			if (selection != null && !"".equals(selection)) 
				whereClause = whereClause + " and " + selection;
			num = db.update(StaticSettings.DEFAULT_TABLE_NAME, 
					values, whereClause, selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Uri:" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return num;
	}

}
