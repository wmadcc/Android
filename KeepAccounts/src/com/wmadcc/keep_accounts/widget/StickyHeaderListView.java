package com.wmadcc.keep_accounts.widget;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wmadcc.keep_accounts.AccountsItem;
import com.wmadcc.keep_accounts.R;

public class StickyHeaderListView extends FrameLayout {
	
//	private final static float MAX_ALPHA = 1;
	
	private WrapperViewList mListView;
	private View mHeader;
	private View mNoRecordHint;
	
	private Long mHeaderId;
    private int mHeaderOffset;
    
	private StickyHeaderListBaseAdapter mAdapter;
	private AdapterWrapperDataSetObserver mDataSetObserver;
	
	public StickyHeaderListView(Context context) {
		this(context, null);
	}

	public StickyHeaderListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickyHeaderListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mListView = new WrapperViewList(context);
		addView(mListView);
	}
	
	public void setAdapter(StickyHeaderListBaseAdapter adapter) {
        if (adapter == null) {
        	mListView.setAdapter(null);
            clearHeader();
            addNoRecordHint();
            return;
        }
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        mAdapter = adapter;
        mDataSetObserver = new AdapterWrapperDataSetObserver();
        mAdapter.registerDataSetObserver(mDataSetObserver);
        mListView.setAdapter(mAdapter);
        clearHeader();
        addNoRecordHint();
	}
		
	public StickyHeaderListBaseAdapter getAdapter() {
		return mAdapter;
	}
	
    public void setOnItemClickListener(OnItemClickListener listener) {
    	mListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    	mListView.setOnItemLongClickListener(listener);
    }
    
    public void setOnScrollListener(OnScrollListener onScrollListener) {
    	mListView.setOnScrollListener(onScrollListener);
    }
	
    public void configureHeader(int position) {
    	updateHeader(position);
    	if (mHeader != null) {
    		int headerOffset = 0;
    		int nextSectionVisiblePosiotion = 0;
    		int headerHeight = mHeader.getMeasuredHeight();
    		int visibleChildCount = mListView.getChildCount();
    		
    		for (int i = 0; i < visibleChildCount; i++) {
    			nextSectionVisiblePosiotion = i;
    			if (mAdapter.getHeaderId(position + i) != mHeaderId) {
    				break;
    			}	
    		}
    		headerOffset = Math.min(mListView
    				.getChildAt(nextSectionVisiblePosiotion)
    				.getTop() - headerHeight, 0);
    		
    		if (headerOffset != mHeaderOffset) {
    			mHeaderOffset = headerOffset;
                mHeader.layout(0, mHeaderOffset, mHeader.getMeasuredWidth(), 
                		mHeaderOffset + mHeader.getMeasuredHeight());
    		}
    	}
    }
    
    public void updateHeader(int position) {
    	if (mAdapter == null || mAdapter.getCount() == 0) {
    		clearHeader();
    		return;
    	}
    	
    	if (mHeaderId == null || mHeaderId !=
    			mAdapter.getHeaderId(position)) {
    		clearHeader();
    		mHeaderId = mAdapter.getHeaderId(position);
    		mHeader = mAdapter.getHeaderView(position, mHeader, this);
            ensureHeaderHasCorrectLayoutParams(mHeader);
            measureHeader(mHeader);
    		addView(mHeader);
//    		mHeader.setVisibility(VISIBLE);
//    		mHeader.setAlpha(MAX_ALPHA);
    	}
    }
    
    private void clearHeader() {
        if (mHeader != null) {
            removeView(mHeader);
            mHeader = null;
            mHeaderId = null;
            mHeaderOffset = 0;
        }
    }
        
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeader(mHeader);
    }
    
    private void ensureHeaderHasCorrectLayoutParams(View header) {
    	if (header != null) {
	        ViewGroup.LayoutParams lp = header.getLayoutParams();
	        lp = new LayoutParams(LayoutParams.MATCH_PARENT, 
	        		LayoutParams.WRAP_CONTENT);
	        header.setLayoutParams(lp);
    	}
    }
    
    private void measureHeader(View header) {
        if (header != null) {
            final int width = getMeasuredWidth();
            final int parentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    width, MeasureSpec.EXACTLY);
            final int parentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
            measureChild(header, parentWidthMeasureSpec,
                    parentHeightMeasureSpec);
        }
    }
    
    private void addNoRecordHint() {
    	if (mAdapter == null 
    			|| mAdapter.getCount() == 0) {
	    	LayoutInflater inflater = 
	    			LayoutInflater.from(getContext());
	    	mNoRecordHint = inflater
	    			.inflate(R.layout.no_record_hint, null);
	    	TextView noRecordTextView = (TextView) mNoRecordHint
	    			.findViewById(R.id.noRecordHintView);
	    	noRecordTextView.setText(R.string.no_record_hint_text);
	    	mNoRecordHint.setVisibility(VISIBLE);
	    	addView(mNoRecordHint);
    	}
    }
    
    private void removeNoRecordHint() {
    	if (mNoRecordHint != null) {
    		removeView(mNoRecordHint);
    		mNoRecordHint.setVisibility(GONE);
    		mNoRecordHint = null;
    	}
    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mListView.layout(0, 0, mListView.getMeasuredWidth(), 
        		getHeight());
        if (mHeader != null) {
            mHeader.layout(0, mHeaderOffset, mHeader.getMeasuredWidth(), 
            		mHeaderOffset + mHeader.getMeasuredHeight());
        }
        if (mNoRecordHint != null) {
        	mNoRecordHint.layout(0, 0, mNoRecordHint.getMeasuredWidth(), 
        			mNoRecordHint.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mListView.getVisibility() == VISIBLE || mListView.getAnimation() != null) {
            drawChild(canvas, mListView, 0);
        }
    }
    
    @Override
    public boolean isVerticalScrollBarEnabled() {
        return mListView.isVerticalScrollBarEnabled();
    }

    @Override
    public boolean isHorizontalScrollBarEnabled() {
        return mListView.isHorizontalScrollBarEnabled();
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
    	super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    	mListView.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
    	super.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    	mListView.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }
    
    public void setDividerHeight(int height) {
    	mListView.setDividerHeight(height);
    }
    
    public int getCount() {
    	return mListView.getCount();
    }
    
    public Object getItem(int position) {
    	return mAdapter.getItem(position);
    }
    
    public void refresh(ArrayList<AccountsItem> itemsList) {
    	mAdapter.refresh(itemsList);
    }
    
    private class AdapterWrapperDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            clearHeader();
            removeNoRecordHint();
            addNoRecordHint();
        }

        @Override
        public void onInvalidated() {
            clearHeader();
            removeNoRecordHint();
            addNoRecordHint();
        }

    }
    
    private class WrapperViewList extends ListView {

		public WrapperViewList(Context context) {
			super(context);
		}
		
		@Override
		protected void dispatchDraw(Canvas canvas) { 
			super.dispatchDraw(canvas);
            configureHeader(getFirstVisiblePosition());
            if (mHeader != null) {
            	drawChild(canvas, mHeader, 0);
            }
			if (mNoRecordHint != null) {
				drawChild(canvas, mNoRecordHint, 0);
			}
		}
		
    }
    
}
