package com.wmadcc.keep_accounts.widget;

import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.wmadcc.keep_accounts.R;

public class MonthPickerDialog extends DatePickerDialog {
	
	public MonthPickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		setTitle(R.string.set_month_text);
		
		int day_position = this.isZh() ? 2 : 1;		
		((ViewGroup) ((ViewGroup) getDatePicker()
				.getChildAt(0)).getChildAt(0))
				.getChildAt(day_position)
				.setVisibility(View.GONE);
	}

	public MonthPickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		setTitle(R.string.set_month_text);
		
		int day_position = this.isZh() ? 2 : 1;		
		((ViewGroup) ((ViewGroup) getDatePicker()
				.getChildAt(0)).getChildAt(0))
				.getChildAt(day_position)
				.setVisibility(View.GONE);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
	
	@Override
	public void onDateChanged(DatePicker view, 
			int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
		setTitle(R.string.set_month_text);
	}
	
	private boolean isZh() {
        Locale locale = getContext()
        		.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return (language.startsWith("zh"));
    }

}
