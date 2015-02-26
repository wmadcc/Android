package com.wmadcc.keep_accounts;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryDetailActivity extends Activity {
	
	int mYear, mMonth;
	boolean mIsEarning;
	String mCategoryName;
	CategoryItemWrapper mCategoryItem;	
	
	ListView mCategoryDetailList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_detail);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mYear = bundle.getInt(StaticSettings.YEAR);
		mMonth = bundle.getInt(StaticSettings.MONTH);
		mIsEarning = bundle.getBoolean(StaticSettings.IS_EARNING);
		mCategoryName = bundle.getString(StaticSettings.CATEGORY);
		
		((TextView) findViewById(R.id.returnMonthSummary))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();				
			}			
		});
		initCategoryItem();
		initCategorySum();
		initCategoryDetailList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	private void refresh() {
		initCategoryItem();
		initCategorySum();
		((BaseAdapter) mCategoryDetailList
				.getAdapter()).notifyDataSetChanged();
	}
	
	private void initCategoryItem() {
		ContentResolver resolver = getContentResolver();
		Uri queryUri = Uri.parse("content://" + StaticSettings.AUTHORITY
				+ "/" + StaticSettings.ALL_ITEMS);		
		int earningValue = mIsEarning ? 1 : 0;
		
		Cursor cursor = resolver.query(queryUri, null, 
				StaticSettings.YEAR + "=? AND "
				+ StaticSettings.MONTH + "=? AND "
				+ StaticSettings.IS_EARNING + "=?",
				new String[]{mYear + "", mMonth + "", 
				earningValue + ""}, null);
		
		ArrayList<AccountsItem> itemsList = 
				convertCursorToItemsList(cursor);
		cursor.close();
		
		double valueSum, valueTarget;
		valueSum = valueTarget = 0;
		int itemCount = itemsList.size();
		for(int i = 0; i < itemCount; i++) {
			AccountsItem item = itemsList.get(i);
			double value = item.getValue();
			valueSum += value;
			if (mCategoryName.equals(item.getCategory())) {
				valueTarget += value;
			}
		}
		float ratio = 1.000f;
		if (valueSum > 0.005d) {
			ratio = (float) (valueTarget / valueSum);
		}
		mCategoryItem = new CategoryItemWrapper(mCategoryName, 
				valueTarget, ratio, mIsEarning);
	}
	
	private void initCategorySum() {
		boolean isEarning = mCategoryItem.isEarning();
		String categoryName = mCategoryItem.getCategory();
		double value = mCategoryItem.getValue();
		float ratio = mCategoryItem.getRatio();
		Drawable categoryIcon;
		CustomTypesEditor customTypesEditor = new CustomTypesEditor(this);
		
		LayoutInflater layoutInflater = this.getLayoutInflater();
		View categorySum = layoutInflater
				.inflate(R.layout.category_list_item, null);
		ImageView categoryImg = (ImageView) categorySum
				.findViewById(R.id.categoryImage);
		TextView categoryTv = (TextView) categorySum
				.findViewById(R.id.categoryText);
		TextView categoryRatio = (TextView) categorySum
				.findViewById(R.id.categoryRatio);
		ProgressBar categoryRatioBar = (ProgressBar) categorySum
				.findViewById(R.id.categoryRatioBar);
		TextView costValueTv = (TextView) categorySum
				.findViewById(R.id.costValue);

		categoryIcon = customTypesEditor
				.getCategoryDrawabe(isEarning, categoryName);
		
		categoryImg.setImageDrawable(categoryIcon);
		categoryTv.setText(categoryName);
		String categoryRatioText = new DecimalFormat("#0.0%")
				.format(ratio);
		categoryRatio.setText(categoryRatioText);

		String costValueText = new DecimalFormat("#,###,###,##0.00")
				.format(value);
		
		if (isEarning) {
			categoryImg.setBackground(getResources()
					.getDrawable(R.color.earning_color));
			costValueTv.setText("+" + costValueText);
			categoryRatioBar.setProgressDrawable(
					getResources().getDrawable(R
							.drawable.earning_ratio_bar));
		} else {
			categoryImg.setBackground(getResources()
					.getDrawable(R.color.expense_color));
			costValueTv.setText("-" + costValueText);
			categoryRatioBar.setProgressDrawable(
					getResources().getDrawable(R
							.drawable.expense_ratio_bar));
		}
		
		int ratioBarProgress = (int) (ratio * 100);
		if (ratioBarProgress < 3) {
			ratioBarProgress = 3;
		}
		categoryRatioBar.setProgress(ratioBarProgress);
		
		RelativeLayout categorySumLayout = (RelativeLayout) 
				findViewById(R.id.categoryItemSummary);
		categorySumLayout.removeAllViews();
		categorySumLayout.addView(categorySum);		
	}
	
	private void initCategoryDetailList() {
		((TextView) findViewById(R
				.id.categoryDetailListTitle))
				.setText(mYear + "-" + mMonth);
		mCategoryDetailList = (ListView) 
				findViewById(R.id.categoryDetailList);
		
		BaseAdapter adapter = new BaseAdapter() {
			
			ArrayList<AccountsItem> itemlist = getItemsList();
			
			@Override
			public int getCount() {
				return itemlist.size();
			}

			@Override
			public Object getItem(int position) {
				return itemlist.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				AccountsItem item = itemlist.get(position);
				LayoutInflater inflater = LayoutInflater
						.from(CategoryDetailActivity.this);
				convertView = inflater.inflate(
						R.layout.category_detail_list_item, null);
				
				TextView itemDay = (TextView) convertView
						.findViewById(R.id.itemDay);
				TextView itemDetail = (TextView) convertView
						.findViewById(R.id.itemDetail);
				TextView itemValue = (TextView) convertView
						.findViewById(R.id.itemValue);
				
				itemDay.setText(item.getDay() + "»’");
				String detail = item.getDetail();
				if (detail == null || "".equals(detail)) {
					detail = item.getCategory();
				}
				itemDetail.setText(detail);
				String itemValueText = new DecimalFormat("#,###,###,##0.00")
						.format(item.getValue());
				if (item.getIsEarning()) {
					itemValue.setText("+" + itemValueText);
				} else {
					itemValue.setText("-" + itemValueText);
				}
				
				return convertView;
			}
			
			@Override
			public void notifyDataSetChanged() {
				super.notifyDataSetChanged();
				itemlist = getItemsList();				
			}
			
		};
		mCategoryDetailList.setAdapter(adapter);
		mCategoryDetailList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BaseAdapter adapter = (BaseAdapter) mCategoryDetailList.getAdapter();
				AccountsItem item = (AccountsItem) adapter.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable(StaticSettings.ACCOUNTS_ITEM_NAME, item);
				Intent intent = new Intent(CategoryDetailActivity.this, 
						AddOrModifyItemActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}			
		});
		mCategoryDetailList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				BaseAdapter adapter = (BaseAdapter) mCategoryDetailList.getAdapter();
				AccountsItem item = (AccountsItem) adapter.getItem(position);
				AlertDialog.Builder builder = new AlertDialog
						.Builder(CategoryDetailActivity.this);
				final int itemId = item.getId();
				String detail = item.getDetail();
				if (detail == null || "".equals(detail)) {
					detail = item.getCategory();
				}
				builder.setTitle(detail);
				builder.setCancelable(true);
				String deleteText = getText(R.string.delete_text).toString();
				builder.setItems(new CharSequence[] {deleteText}, 
						new DialogInterface.OnClickListener() {							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which == 0) {
									ContentResolver resolver = getContentResolver();
									Uri deleteUri = Uri.parse("content://" + StaticSettings.AUTHORITY
											+ "/" + StaticSettings.SINGLE_ITEM + "/" + itemId);	
									resolver.delete(deleteUri, null, null);
									refresh();
								}
								dialog.dismiss();
							}
						});
				builder.create().show();
				return false;
			}			
		});
	}

	private ArrayList<AccountsItem> getItemsList() {
		ContentResolver resolver = getContentResolver();
		Uri queryUri = Uri.parse("content://" + StaticSettings.AUTHORITY
				+ "/" + StaticSettings.ALL_ITEMS);		
		boolean isEarning = mCategoryItem.isEarning();
		String categoryName = mCategoryItem.getCategory();
		int earningValue = isEarning ? 1 : 0;
		
		Cursor cursor = resolver.query(queryUri, null, 
				StaticSettings.YEAR + "=? AND "
				+ StaticSettings.MONTH + "=? AND "
				+ StaticSettings.IS_EARNING + "=? AND "
				+ StaticSettings.CATEGORY + "=?", 
				new String[]{mYear + "", mMonth + "", 
				earningValue + "", categoryName}, null);
		
		ArrayList<AccountsItem> itemsList = 
				convertCursorToItemsList(cursor);
		cursor.close();
		
		return itemsList;
	}
	
	private ArrayList<AccountsItem> convertCursorToItemsList(
			Cursor cursor) {
		ArrayList<AccountsItem> accountsItemsList = 
				new ArrayList<AccountsItem>();
		
		int id, year, month, day;
		boolean isEarning, isMarked;
		double value;
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
							.IS_EARNING)) != 0;
			category = cursor.getString(
					cursor.getColumnIndex(
							StaticSettings.CATEGORY));
			wayOfPayment = cursor.getString(
					cursor.getColumnIndex(
							StaticSettings.WAY_OF_PAYMENT));
			value = cursor.getDouble(
					cursor.getColumnIndex(
							StaticSettings.VALUE));
			isMarked = cursor.getInt(
					cursor.getColumnIndex(
							StaticSettings
							.IS_MARKED)) != 0;
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
	
}
