package com.wmadcc.keep_accounts;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailInputActivity extends Activity {
	
	EditText detailInputDetail;
	AccountsItem mAccountsItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_input);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mAccountsItem = (AccountsItem) bundle
					.get(StaticSettings.ACCOUNTS_ITEM_NAME);
		}
		
		int year = mAccountsItem.getYear();
		int month = mAccountsItem.getMonth();
		int day = mAccountsItem.getDay();
		String[] dayOfWeekArray = getResources()
				.getStringArray(R.array.day_of_week);
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1 , day);
		String dayOfWeekText = 
				dayOfWeekArray[calendar
				               .get(Calendar.DAY_OF_WEEK) - 1];
		StringBuffer dateWayBuffer = new StringBuffer();
		dateWayBuffer.append(month + "ÔÂ"
						+ day + "ÈÕ " + dayOfWeekText);
		String way = mAccountsItem.getWayOfPayment();
		if (way != null && !"".equals(way)) {
			dateWayBuffer.append("/" + way);
		}		
		((TextView) findViewById(R.id
				.detailInputDateAndWay))
				.setText(dateWayBuffer.toString());
		
		boolean isEarning = mAccountsItem.getIsEarning();
		String categoryName = mAccountsItem.getCategory();
		String detail = mAccountsItem.getDetail();
		CustomTypesEditor customTypesEditor = 
				new CustomTypesEditor(this);
		Drawable categoryIcon = customTypesEditor
				.getCategoryDrawabe(isEarning, categoryName);
		
		LayoutInflater layoutInflater = getLayoutInflater();
		View itemView = layoutInflater
				.inflate(R.layout.detail_list_item, null);
		ImageView categoryImage = (ImageView) itemView
				.findViewById(R.id.categoryImage1);
		TextView costValue = (TextView) itemView
				.findViewById(R.id.costValue1);
		TextView costDeatil = (TextView) itemView
				.findViewById(R.id.costDeatil1);

		categoryImage.setImageDrawable(categoryIcon);
		if (isEarning) {
			categoryImage.setBackground(getResources()
					.getDrawable(R.color.earning_color));
		} else {
			categoryImage.setBackground(getResources()
					.getDrawable(R.color.expense_color));
		}		
		String valueText = new DecimalFormat("#,###,###,##0.00")
				.format(mAccountsItem.getValue());
		if (isEarning) {
			costValue.setText("+" + valueText);
		} else {
			costValue.setText("-" + valueText);
		}
		if (detail != null && !"".equals(detail)) {
			costDeatil.setText(detail);
		} else {
			costDeatil.setText(categoryName);
		}
		((RelativeLayout) findViewById(
				R.id.detailInputCategoryAndValue)).addView(itemView);
				
		((TextView) findViewById(R.id
				.detailReturn)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}					
				});
		
		((TextView) findViewById(R.id
				.detailComplete)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = getIntent();
						String detail = detailInputDetail.getText().toString();
						intent.putExtra(StaticSettings.DETAIL, 
								detail);
						setResult(StaticSettings.DETAIL_CODE, intent);
						finish();
					}					
				});
		
		detailInputDetail = (EditText) findViewById(R.id.detaiInputDetail);
		if (detail != null && !"".equals(detail)) {
			detailInputDetail.setText(detail);
			detailInputDetail.requestFocus();
		};
	}
}
