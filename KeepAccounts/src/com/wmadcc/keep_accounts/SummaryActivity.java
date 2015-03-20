package com.wmadcc.keep_accounts; 

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmadcc.keep_accounts.widget.MonthDetailFragment;
import com.wmadcc.keep_accounts.widget.MonthPickerDialog;
import com.wmadcc.keep_accounts.widget.MonthSummaryFragment;

public class SummaryActivity extends Activity {

	int mYear, mMonth;
	String shownFragment;
	
	RelativeLayout addItemLayout;
	TextView earningSumTextView, expenseSumTextView;
	LinearLayout dateSelect;
	TextView yearTextView, monthTextView;
	TextView monthDetailTitle, monthSummayTitle;
	ImageView menuIcon;
	
	FragmentManager fragmentManager;
	MonthDetailFragment mMonthDetailFragment;
	MonthSummaryFragment mMonthSummaryFragment;
	PopupMenu mPopupMenu;
	
	private final static String 
			MONTH_DETAIL_FRAGMENT = "MonthDetailFragment",
			MONTH_SUMMARY_FRAGMENT = "MonthSummaryFragment";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        
    	Time time = new Time();
    	time.setToNow();
    	mYear = time.year;
    	mMonth = time.month + 1;
            	
    	menuIcon = (ImageView) findViewById(R.id.menuPopup);
    	initMenu();
    	menuIcon.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View source) {
    			mPopupMenu.show();
    		}
    	});
    	
        earningSumTextView = (TextView) findViewById(
        		R.id.earningTextView);
        expenseSumTextView = (TextView) findViewById(
        		R.id.expenseTextView);
        
        dateSelect = (LinearLayout) findViewById(
        		R.id.dateSelect);
        
        yearTextView = (TextView) dateSelect
        		.findViewById(R.id.yearTextView);
        monthTextView = (TextView) dateSelect
        		.findViewById(R.id.monthTextView);
        
        addItemLayout = (RelativeLayout) findViewById(R.id.addNewItem);
        addItemLayout.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View source) {
        		Intent intent = new Intent(SummaryActivity.this,
        				AddOrModifyItemActivity.class);
        		startActivity(intent);
        	}
        });
                
        dateSelect.setOnClickListener(new OnClickListener() {
        	public void onClick(View source) {
        		MonthPickerDialog monthSelect = 
        				new MonthPickerDialog(SummaryActivity.this,
        						new MonthPickerDialog.OnDateSetListener() {
        			@Override
        			public void onDateSet(DatePicker view, 
        					int year, int monthOfYear, int dayOfMonth) {
        			}
        		}, mYear, mMonth - 1, 1);
        		monthSelect.setCancelable(true);
        		monthSelect.setCanceledOnTouchOutside(true);
        		monthSelect.setButton(DialogInterface.BUTTON_NEGATIVE, 
        				getText(R.string.cancel_text),
        				new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				dialog.cancel();
        			}
        		});
        		monthSelect.setButton(DialogInterface.BUTTON_POSITIVE,
        				getText(R.string.confirm_text),
        				new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				DatePicker datePicker = ((MonthPickerDialog)dialog)
        						.getDatePicker();
        				int setYear = datePicker.getYear();
        				int setMonth = datePicker.getMonth() + 1;
        				if (setYear != mYear || setMonth != mMonth)
        					setSelectedMonth(setYear, setMonth);
        			}
        		});
        		monthSelect.show();
        	}
        });
        
        monthDetailTitle = (TextView) 
        		findViewById(R.id.monthDeatilTitle);
        monthSummayTitle = (TextView) 
        		findViewById(R.id.monthSummarylTitle);
        monthDetailTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View sourec) {
				setTitleToDetail();
				setShownFragmentToDetail();
			}
        });
        monthSummayTitle.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View source) {
        		setTitleToSummary();
        		setShownFragmentToSummary();
        	}
        });
        
        fragmentManager = getFragmentManager();
        mMonthDetailFragment = new MonthDetailFragment();
        mMonthSummaryFragment = new MonthSummaryFragment();
        
        addFragments();
        setTitleToDetail();
        setShownFragmentToDetail(false);
    }
    
    private void initMenu() {
    	mPopupMenu = new PopupMenu(this, menuIcon);
    	MenuInflater inflater = mPopupMenu.getMenuInflater();
    	inflater.inflate(R.menu.summary_option_menu, mPopupMenu.getMenu());
    	mPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem mi) {
		    	if (mi.isCheckable())
		    		mi.setChecked(true);
		    	switch (mi.getItemId()) {
		    	case R.id.trend_analysis:
		    		mPopupMenu.dismiss();
		    		break;
		    		
		    	case R.id.quit:
		    		finish();
		    		break;
		    	
		    	default:
		    		break;
		    	}
		    	return true;
			}
    		
    	});
    }
    
    private void addFragments() {
		FragmentTransaction transaction = 
				fragmentManager.beginTransaction();
        transaction.add(R.id.monthSummaryDetail, 
        		(Fragment) mMonthSummaryFragment,
        		MONTH_DETAIL_FRAGMENT);
        transaction.hide(mMonthSummaryFragment);
        transaction.add(R.id.monthSummaryDetail, 
        		(Fragment) mMonthDetailFragment,
        		MONTH_DETAIL_FRAGMENT);
        transaction.hide(mMonthDetailFragment);
        transaction.commit();
    }
    
    private void setShownFragmentToDetail(boolean updateDisplay) {
		if (!shownFragment.equals(MONTH_DETAIL_FRAGMENT)) {
			FragmentTransaction transaction = 
					fragmentManager.beginTransaction();
			transaction.show(mMonthDetailFragment);
			transaction.hide(mMonthSummaryFragment);
	        transaction.commit();
	        shownFragment = MONTH_DETAIL_FRAGMENT;
	        if (updateDisplay) {
	        	refreshFragment();
	        }
		}
    }
    
    private  void setShownFragmentToDetail() {
    	setShownFragmentToDetail(true);
    }
    
    private void setShownFragmentToSummary(boolean updateDisplay) {
		if (!shownFragment.equals(MONTH_SUMMARY_FRAGMENT)) {
			FragmentTransaction transaction = 
					fragmentManager.beginTransaction();
			transaction.show(mMonthSummaryFragment);
			transaction.hide(mMonthDetailFragment);
	        transaction.commit();
	        shownFragment = MONTH_SUMMARY_FRAGMENT;
	        if (updateDisplay) {
	        	refreshFragment();
	        }
		}
    }
    
    private void setShownFragmentToSummary() {
    	setShownFragmentToSummary(true);
    }
    
    private void setTitleToDetail() {
    	monthDetailTitle.setTextColor(
    			getResources().getColor(R.color.grey_bg_color));
    	monthDetailTitle.setBackgroundColor(
    			getResources().getColor(R.color.white));
    	monthSummayTitle.setTextColor(
    			getResources().getColor(R.color.white));
    	monthSummayTitle.setBackgroundColor(
    			getResources().getColor(R.color.grey_bg_color));
    }
    
    private void setTitleToSummary() {
    	monthSummayTitle.setTextColor(
    			getResources().getColor(R.color.grey_bg_color));
    	monthSummayTitle.setBackgroundColor(
    			getResources().getColor(R.color.white));
    	monthDetailTitle.setTextColor(
    			getResources().getColor(R.color.white));
    	monthDetailTitle.setBackgroundColor(
    			getResources().getColor(R.color.grey_bg_color));
    }
    
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_MENU) {
    		menuIcon.performClick();
    		return false;
    	}
		return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
      	setSelectedMonth(mYear, mMonth);
    }
           
    private void setSelectedMonth(int year, int month) {
    	mYear = year;
    	mMonth = month;
    	yearTextView.setText(year + "Äê");
		monthTextView
			.setText(month > 9 ? month + "" : "0" + month);		
		refreshFragment();
    }
    
    public void refreshFragment() {
    	DecimalFormat sumDisplayFormat = new DecimalFormat("#,###,###,##0.00");
    	if (shownFragment.equals(MONTH_DETAIL_FRAGMENT)) {
    		mMonthDetailFragment.setYearAndMonth(mYear, mMonth);
    		earningSumTextView
    		.setText(sumDisplayFormat
    				.format(mMonthDetailFragment.getSumEarning()));
    		expenseSumTextView
    		.setText(sumDisplayFormat
    				.format(mMonthDetailFragment.getSumPayment()));
    	} else if (shownFragment.equals(MONTH_SUMMARY_FRAGMENT)) {
    		mMonthSummaryFragment.setYearAndMonth(mYear, mMonth);
    		earningSumTextView
    		.setText(sumDisplayFormat
    				.format(mMonthSummaryFragment.getSumEarning()));
    		expenseSumTextView
    		.setText(sumDisplayFormat
    				.format(mMonthSummaryFragment.getSumPayment()));
    	}
    }
    
}
