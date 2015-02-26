package com.wmadcc.keep_accounts.widget;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.TextView;

import com.wmadcc.keep_accounts.AccountsItem;
import com.wmadcc.keep_accounts.AddOrModifyItemActivity;
import com.wmadcc.keep_accounts.CustomTypesEditor;
import com.wmadcc.keep_accounts.R;
import com.wmadcc.keep_accounts.StaticSettings;
import com.wmadcc.keep_accounts.SummaryActivity;

public class MonthDetailFragment extends Fragment {
	
	private ContentResolver mResolver;
	private int mYear, mMonth;
	private Context mContext;
	private StickyHeaderListView mStickyHeaderListView;
	private StickyHeaderItemsListAdapter mAdapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		System.out.println("--MonthDetailFragment--created--");
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mResolver = getActivity().getContentResolver();
        
    	Time time = new Time();
    	time.setToNow();
		mYear = time.year;
 		mMonth = time.month + 1;
        
		mStickyHeaderListView = 
				new StickyHeaderListView(mContext);
		mAdapter = new StickyHeaderItemsListAdapter(mContext, getItemsList());
		mStickyHeaderListView.setAdapter(mAdapter);
		mStickyHeaderListView.setVerticalScrollBarEnabled(false);
		mStickyHeaderListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				StickyHeaderItemsListAdapter adapter = 
						(StickyHeaderItemsListAdapter) mStickyHeaderListView.getAdapter();
				if (adapter.getItemViewType(position) == 
						StickyHeaderItemsListAdapter.TYPE_ITEM) {
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
				StickyHeaderItemsListAdapter adapter = 
						(StickyHeaderItemsListAdapter) mStickyHeaderListView.getAdapter();
				if (adapter.getItemViewType(position) == 
						StickyHeaderItemsListAdapter.TYPE_ITEM) {
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
		System.out.println("--MonthDetailFragment--View--created--");
		return mStickyHeaderListView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		System.out.println("--MonthDetailFragment--attached--");
	}
		
	public void setYearAndMonth(int year, int month) {
		this.mYear = year;
		this.mMonth = month;
		refresh();
	}
			
	public double getSumEarning() {
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
	
	private class StickyHeaderItemsListAdapter 
		extends StickyHeaderListBaseAdapter {
		
		private double sumEarning, sumPayment;
		
		private final static int TYPE_SECTION_HEADER = 0;
		private final static int TYPE_ITEM = 1;
		
		private LayoutInflater layoutInflater;
		
		public StickyHeaderItemsListAdapter(Context context,
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
		
		@Override
		protected void createShowItemsList() {
			clearShownList();
			sumEarning = sumPayment = 0;
			
			ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
			ArrayList<String> sectionHeaders = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			AccountsItem accountsItem;
			
			int previousYear = 0, previousMonth = 0, previousDay = 0;
			int itemYear = 0, itemMonth = 0, itemDay = 0;
			String[] dayOfWeekArray = mContext.getResources()
					.getStringArray(R.array.day_of_week);
			int sourceDataSize = getPrimaryItemsCount();
			
			for (int i = 0, j = 0; i < sourceDataSize; i++) {
				accountsItem = getPrimaryItem(i);
				itemYear = accountsItem.getYear();
				itemMonth = accountsItem.getMonth();
				itemDay = accountsItem.getDay();
				
				if (accountsItem.getIsEarning()) {
					sumEarning += accountsItem.getValue();
				} else {
					sumPayment += accountsItem.getValue();
				}
				
				if (!(previousDay == itemDay && previousMonth == itemMonth
						&& previousYear == itemYear)) {		
					previousDay = itemDay;
					previousMonth = itemMonth;
					previousYear = itemYear;
							
					calendar.set(itemYear, itemMonth - 1, itemDay);
					
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append((itemDay > 9 ? itemDay 
							: "0" + itemDay ) + "»’-");
					String dayOfWeekText = 
							dayOfWeekArray[calendar
							               .get(Calendar.DAY_OF_WEEK) - 1];
					stringBuffer.append(dayOfWeekText);
					String sectionHeader = stringBuffer.toString();
					sectionHeaders.add(sectionHeader);
					
					addShowItem(sectionHeader);
					sectionIndices.add(j);
					j++;
				}
				
				addShowItem(accountsItem);
				j++;
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
			if (getCount() == 0 || position < 0
					|| position > getCount())
				try {
					throw new Exception("getItemViewType ERROR");
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			if (isSectionHeader(position))
				return TYPE_SECTION_HEADER;
			else
				return TYPE_ITEM;
		}
				
		@Override
		public int getViewTypeCount() {  
			return 2;
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
			
			switch (itemViewType) {
			case TYPE_ITEM:
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
				
				boolean isEarning = accountsItem.getIsEarning();
				String categoryName = accountsItem.getCategory();
				String detail = accountsItem.getDetail();
				
				CustomTypesEditor customTypesEditor = 
						new CustomTypesEditor((Activity) mContext);
				Drawable categoryIcon = customTypesEditor
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
				
			case TYPE_SECTION_HEADER:
				String sectionHeader = (String) getItem(position);
				convertView = layoutInflater
						.inflate(R.layout.list_title, null);
				TextView textView = (TextView) convertView
						.findViewById(R.id.detailItemTitle);			
				textView.setText(sectionHeader);
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
