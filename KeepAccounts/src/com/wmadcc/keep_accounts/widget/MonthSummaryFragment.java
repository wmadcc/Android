package com.wmadcc.keep_accounts.widget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wmadcc.keep_accounts.AccountsItem;
import com.wmadcc.keep_accounts.AddOrModifyItemActivity;
import com.wmadcc.keep_accounts.CategoryDetailActivity;
import com.wmadcc.keep_accounts.CategoryItemWrapper;
import com.wmadcc.keep_accounts.CustomTypesEditor;
import com.wmadcc.keep_accounts.R;
import com.wmadcc.keep_accounts.StaticSettings;
import com.wmadcc.keep_accounts.SummaryActivity;

public class MonthSummaryFragment extends Fragment {

	private ContentResolver mResolver;
	private int mYear, mMonth;
	private Context mContext;
	private StickyHeaderListView mStickyHeaderListView;
	private StickyHeaderClassifedListAdapter mAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		System.out.println("--MonthSummaryFragment--created--");
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mResolver = getActivity().getContentResolver();
        
    	Time time = new Time();
    	time.setToNow();
		mYear = time.year;
 		mMonth = time.month + 1;
 		
		mStickyHeaderListView = 
				new StickyHeaderListView(mContext);
		mAdapter = new StickyHeaderClassifedListAdapter(mContext, getItemsList());
		mStickyHeaderListView.setAdapter(mAdapter);
		mStickyHeaderListView.setVerticalScrollBarEnabled(false);
		mStickyHeaderListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				StickyHeaderClassifedListAdapter adapter = 
						(StickyHeaderClassifedListAdapter) mStickyHeaderListView
						.getAdapter();
				int itemType = adapter.getItemViewType(position);
				if (itemType == StickyHeaderClassifedListAdapter.TYPE_CATEGORY_ITEM) {
					CategoryItemWrapper item = 
							(CategoryItemWrapper) mStickyHeaderListView.getItem(position);
					Intent intent = new Intent(mContext, CategoryDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(StaticSettings.YEAR, mYear);
					bundle.putInt(StaticSettings.MONTH, mMonth);
					bundle.putBoolean(StaticSettings.IS_EARNING, item.isEarning());
					bundle.putString(StaticSettings.CATEGORY, item.getCategory());
					intent.putExtras(bundle);
					startActivity(intent);
				} else if(itemType == StickyHeaderClassifedListAdapter.TYPE_MARKED_ITEM) {
					AccountsItem item = 
							(AccountsItem) mStickyHeaderListView.getItem(position);
					Intent intent = new Intent(mContext, AddOrModifyItemActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(StaticSettings.ACCOUNTS_ITEM_NAME, item);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}			
		});
		mStickyHeaderListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				StickyHeaderClassifedListAdapter adapter = 
						(StickyHeaderClassifedListAdapter) mStickyHeaderListView.getAdapter();
				if (adapter.getItemViewType(position) == 
						StickyHeaderClassifedListAdapter.TYPE_MARKED_ITEM) {
					AccountsItem item = 
							(AccountsItem) mStickyHeaderListView.getItem(position);
					final int itemId = item.getId();
					String detail = item.getDetail();
					String dialogTitle;
					if (detail != null && !"".equals(detail)) {
						dialogTitle = detail;
					} else {
						dialogTitle = item.getCategory();
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setCancelable(true);
					builder.setTitle(dialogTitle);
					builder.setItems(new CharSequence[] 
							{getText(R.string.delete_text)}, 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										Uri deleteUri = Uri.parse("content://" + StaticSettings.AUTHORITY
												+ "/" + StaticSettings.SINGLE_ITEM + "/" + itemId);	
										mResolver.delete(deleteUri, null, null);
										((SummaryActivity) mContext).refreshFragment();
									}
									dialog.dismiss();
								}						
					});
					builder.create().show();
				}
				return false;
			}			
		});
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		System.out.println("--MonthSummaryFragment--View--created--");
		return mStickyHeaderListView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		System.out.println("--MonthSummaryFragment--attached--");
	}
	
	public void setYearAndMonth(int year, int month) {
		this.mYear = year;
		this.mMonth = month;
		refresh();
	}
	
	public Double getSumEarning() {
		return mAdapter.getSumEarning();
	}
	
	public double getSumPayment() {
		return mAdapter.getSumPayment();
	}
	
	private ArrayList<AccountsItem> getItemsList() {
		Uri queryUri = Uri.parse("content://" + StaticSettings.AUTHORITY
				+ "/" + StaticSettings.ALL_ITEMS);		
		
		Cursor cursor = mResolver.query(queryUri, null, 
				StaticSettings.YEAR + "=? AND "
				+ StaticSettings.MONTH + "=?", 
				new String[]{mYear + "", mMonth + ""}, null);
		
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
	
	private void refresh() {
		mStickyHeaderListView.refresh(getItemsList());
	}
	
	private class StickyHeaderClassifedListAdapter
		extends StickyHeaderListBaseAdapter {
		
		private double sumEarning, sumPayment;
		
		private final static int TYPE_SECTION_HEADER = 0;
		private final static int TYPE_CATEGORY_ITEM = 1;
		private final static int TYPE_MARKED_ITEM = 2;
				
		private LayoutInflater layoutInflater;
		
		public StickyHeaderClassifedListAdapter(Context context,
				ArrayList<AccountsItem> itemsList) {
			super(context, itemsList);
			layoutInflater = LayoutInflater.from(context);
		}
		
		public double getSumEarning() {
			return sumEarning;
		}
		
		public double getSumPayment() {
			return sumPayment;
		}
		
		private boolean primaryHasEarning() {
			int sourceDataSize = getPrimaryItemsCount();
			AccountsItem accountsItem;
			for (int i = 0; i < sourceDataSize; i++) {
				accountsItem = getPrimaryItem(i);
				if (accountsItem.getIsEarning()) {
					return true;
				}
			}
			return false;
		}
		
		private boolean primaryHasExpense() {
			int sourceDataSize = getPrimaryItemsCount();
			AccountsItem accountsItem;
			for (int i = 0; i < sourceDataSize; i++) {
				accountsItem = getPrimaryItem(i);
				if (!accountsItem.getIsEarning()) {
					return true;
				}
			}
			return false;
		}
		
		private boolean primaryHasMarked() {
			int sourceDataSize = getPrimaryItemsCount();
			AccountsItem accountsItem;
			for (int i = 0; i < sourceDataSize; i++) {
				accountsItem = getPrimaryItem(i);
				if (accountsItem.getIsMarked()) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected void createShowItemsList() {
			clearShownList();
			sumEarning = sumPayment = 0;
			
			ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
			ArrayList<String> sectionHeaders = new ArrayList<String>();	
			AccountsItem accountsItem;
			int itemPosition = 0;
			int sourceDataSize = getPrimaryItemsCount();
			Map<String, Double> sortedExpense = 
					new HashMap<String, Double>();
			Map<String, Double> sortedEarning = 
					new HashMap<String, Double>();
			
			String categoryText;
			double value;
			float ratio;
			
			for (itemPosition = 0; itemPosition < sourceDataSize; 
					itemPosition++) {
				accountsItem = getPrimaryItem(itemPosition);
				categoryText = accountsItem.getCategory();
				value = accountsItem.getValue();
				if (accountsItem.getIsEarning()) {
					sumEarning += value;
					if (sortedEarning.containsKey(categoryText)) {
						value += sortedEarning.get(categoryText);
					} 
					sortedEarning.put(categoryText, value);
				} else {
					sumPayment += value;
					if (sortedExpense.containsKey(categoryText)) {
						value += sortedExpense.get(categoryText);
					} 
					sortedExpense.put(categoryText, value);
				}
			}
			
			int showListPosition = 0;
			
			if (primaryHasExpense()) {
				String expenseCategoryText = mContext
						.getResources().getString(R.string.expense_category_text);
				sectionIndices.add(showListPosition);
				sectionHeaders.add(expenseCategoryText);
				addShowItem(expenseCategoryText);
				showListPosition++;
				
				Iterator<Entry<String, Double>> expenseIter = 
						sortedExpense.entrySet().iterator();
				
				while (expenseIter.hasNext()) {
					Map.Entry<String, Double> entry = 
							(Map.Entry<String, Double>) expenseIter.next();
					categoryText = entry.getKey();
					value = entry.getValue();
					if (sumPayment > 0.005d) {
						ratio = (float) (value / sumPayment);
					} else {
						ratio = 1.000f;
					}
					addShowItem(new CategoryItemWrapper(categoryText, 
							value, ratio, false));
					showListPosition++;
				}
			}
				
			if (primaryHasEarning()) {
				String earningCategoryText = mContext
						.getResources().getString(R.string.earning_category_text);
				sectionIndices.add(showListPosition);
				sectionHeaders.add(earningCategoryText);
				addShowItem(earningCategoryText);
				showListPosition++;	
				
				Iterator<Entry<String, Double>> earningIter = 
						sortedEarning.entrySet().iterator();
				
				while (earningIter.hasNext()) {
					Map.Entry<String, Double> entry = 
							(Map.Entry<String, Double>) earningIter.next();
					categoryText = entry.getKey();
					value = entry.getValue();
					if (sumEarning > 0.005d) {
						ratio = (float) (value / sumEarning);
					} else {
						ratio = 1.000f;
					}
					addShowItem(new CategoryItemWrapper(categoryText, 
							value, ratio, true));
					showListPosition++;
				}			
			}
			
			if (primaryHasMarked()) {
				String markedCategoryText = mContext
						.getResources().getString(R.string.marked_category_text);
				sectionIndices.add(showListPosition);
				sectionHeaders.add(markedCategoryText);
				addShowItem(markedCategoryText);
				for (int i = 0; i < sourceDataSize; i++) {
					accountsItem = getPrimaryItem(i);
					if (accountsItem.getIsMarked()) {
						addShowItem(accountsItem);
					}
				}
			}
			
			int [] newSectionIndices = new int[sectionIndices.size()];
			for (int i = 0; i < newSectionIndices.length; i++) {
				newSectionIndices[i] = sectionIndices.get(i);
			}
			setSectionIndices(newSectionIndices);
			
			String[] newSectionHeaders = new String[sectionHeaders.size()];
			for (int i = 0; i < newSectionHeaders.length; i++) {
				newSectionHeaders[i] = sectionHeaders.get(i);
			}
			setSectionHeaders(newSectionHeaders);
			
		}

		@Override
		public int getItemViewType(int position) { 
			if (isSectionHeader(position)) {
				return TYPE_SECTION_HEADER;
			}

			if (getItem(position) instanceof AccountsItem) {
				return TYPE_MARKED_ITEM;
			}
			
			return TYPE_CATEGORY_ITEM;
		}
				
		@Override
		public int getViewTypeCount() {  
			return 3;
		}
		
		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			if (getPrimaryItemsCount() > 0)
				return getView((int) getHeaderId(position),
						convertView, parent);
			else
				return null;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			boolean isEarning;
			String categoryName, detail;
			Drawable categoryIcon;
			CustomTypesEditor customTypesEditor = 
					new CustomTypesEditor((Activity) mContext);
			
			switch (itemViewType) {
			case TYPE_SECTION_HEADER:
				String sectionHeader = (String) getItem(position);
				convertView = layoutInflater
						.inflate(R.layout.list_title, null);
				TextView textView = (TextView) convertView
						.findViewById(R.id.detailItemTitle);			
				textView.setText(sectionHeader);
				break;
				
			case TYPE_CATEGORY_ITEM:
				CategoryItemWrapper categoryItem = 
						(CategoryItemWrapper) getItem(position);
				convertView = layoutInflater
						.inflate(R.layout.category_list_item, null);
				ImageView categoryImg = (ImageView) convertView
						.findViewById(R.id.categoryImage);
				TextView categoryTv = (TextView) convertView
						.findViewById(R.id.categoryText);
				TextView categoryRatio = (TextView) convertView
						.findViewById(R.id.categoryRatio);
				ProgressBar categoryRatioBar = (ProgressBar) convertView
						.findViewById(R.id.categoryRatioBar);
				TextView costValueTv = (TextView) convertView
						.findViewById(R.id.costValue);
				
				isEarning = categoryItem.isEarning();
				categoryName = categoryItem.getCategory();
				categoryIcon = customTypesEditor
						.getCategoryDrawabe(isEarning, categoryName);
				
				categoryImg.setImageDrawable(categoryIcon);
				categoryTv.setText(categoryName);
				String categoryRatioText = new DecimalFormat("#0.0%")
						.format(categoryItem.getRatio());
				categoryRatio.setText(categoryRatioText);

				String costValueText = new DecimalFormat("#,###,###,##0.00")
						.format(categoryItem.getValue());
				
				if (isEarning) {
					categoryImg.setBackground(getResources()
							.getDrawable(R.color.earning_color));
					costValueTv.setText("+" + costValueText);
					categoryRatioBar.setProgressDrawable(mContext
							.getResources().getDrawable(R
									.drawable.earning_ratio_bar));
				} else {
					categoryImg.setBackground(getResources()
							.getDrawable(R.color.expense_color));
					costValueTv.setText("-" + costValueText);
					categoryRatioBar.setProgressDrawable(mContext
							.getResources().getDrawable(R
									.drawable.expense_ratio_bar));
				}
				
				int ratioBarProgress = (int) (categoryItem.getRatio() * 100);
				if (ratioBarProgress < 3) {
					ratioBarProgress = 3;
				}
				categoryRatioBar.setProgress(ratioBarProgress);
				break;
				
			case TYPE_MARKED_ITEM:
				AccountsItem accountsItem =
				(AccountsItem) getItem(position);
				convertView = layoutInflater
						.inflate(R.layout.detail_list_item, null);
				ImageView categoryImage = (ImageView) convertView
						.findViewById(R.id.categoryImage1);
				TextView costValue = (TextView) convertView
						.findViewById(R.id.costValue1);
				TextView costDeatil = (TextView) convertView
						.findViewById(R.id.costDeatil1);
				
				isEarning = accountsItem.getIsEarning();
				categoryName = accountsItem.getCategory();
				detail = accountsItem.getDetail();
				categoryIcon = customTypesEditor
						.getCategoryDrawabe(isEarning, categoryName);
				categoryImage.setImageDrawable(categoryIcon);
				if (isEarning) {
					categoryImage.setBackground(getResources()
							.getDrawable(R.color.earning_color));
				} else {
					categoryImage.setBackground(getResources()
							.getDrawable(R.color.expense_color));
				}
				
				String valueText = new DecimalFormat("#,###,###,##0.00")
						.format(accountsItem.getValue());
				if (accountsItem.getIsEarning()) {
					costValue.setText("+" + valueText);
				} else {
					costValue.setText("-" + valueText);
				}
				if (detail != null && !"".equals(detail)) {
					costDeatil.setText(detail);
				} else {
					costDeatil.setText(categoryName);
				}
				break;
				
			default:
				break;
			}
			
			return convertView;
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}
		
		@Override
		public boolean isEnabled(int position) { 
			return !isSectionHeader(position);
		}
	}
}
