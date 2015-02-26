package com.wmadcc.keep_accounts.widget;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.wmadcc.keep_accounts.AccountsItem;

public abstract class StickyHeaderListBaseAdapter extends BaseAdapter implements
		SectionIndexer {
	
	private ArrayList<AccountsItem> mItemsList;
	private ArrayList<Object> mShowItemsList;
	private int[] sectionIndices;
	private String[] sectionHeaders;
	
	public StickyHeaderListBaseAdapter(Context context) {
		this(context, null);
	}
	
	public StickyHeaderListBaseAdapter(Context context, 
			ArrayList<AccountsItem> itemsList) {
		mItemsList = new ArrayList<AccountsItem>();
		mShowItemsList = new ArrayList<Object>();
		if (itemsList != null)
			refresh(itemsList);
	}
	
	public void refresh(ArrayList<AccountsItem> itemsList) {
		if (itemsList != null 
				&& !itemsList.equals(mItemsList)) {
			System.out.println("refresh List");
			mItemsList = itemsList;
			createShowItemsList();
			notifyDataSetChanged();
		}
	}
	
	protected abstract void createShowItemsList();
	
	public boolean isSectionHeader(int position) {
		return getItemId(position) == getHeaderId(position);
	}
		
	public int getPrimaryItemsCount() {
		return mItemsList == null ? 0 :
			mItemsList.size();
	}
	
	public AccountsItem getPrimaryItem(int position) {
		return mItemsList == null ? null :
			mItemsList.get(position);
	}
	
	@Override
	public int getCount() {
		return mShowItemsList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return (position >=0 && position < mShowItemsList.size()) ? 
			mShowItemsList.get(position) : null;
	}
	
	public long getHeaderId(int position) {
		return sectionIndices[getSectionForPosition(position)];
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public abstract View getHeaderView(int position, 
			View convertView, ViewGroup parent);
	
	@Override
	public abstract View getView(int position, 
			View convertView, ViewGroup parent);

	@Override
	public Object[] getSections() {
		return sectionHeaders;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		 if (sectionIndex >= sectionIndices.length) {
			 sectionIndex = sectionIndices.length - 1;
	        } else if (sectionIndex < 0) {
	        	sectionIndex = 0;
	        }
	        return sectionIndices[sectionIndex];
	};

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
            if (position < sectionIndices[i]) {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
	}; 
	
	protected void setSectionIndices(int[] newSectionIndices) {
		sectionIndices = newSectionIndices;
	}
	
	protected void setSectionHeaders(String[] newSectionHeaders) {
		sectionHeaders = newSectionHeaders;
	}
	
	protected void clearShownList() {
		mShowItemsList.clear();
		sectionIndices = new int[] {};
		sectionHeaders =  new String[] {};
	}
	
	protected void addShowItem(Object showItem) {
		mShowItemsList.add(showItem);
	}

}
