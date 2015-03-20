package com.wmadcc.keep_accounts;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddOrModifyItemActivity extends Activity {
		
	AccountsItem mAccountsItem;
	
	TextView categoryTypeTitle;
	RelativeLayout titleBar;
	EditText valueInput;
	String[] categoryTypeArray;
	int currentCategoryType = -1;
	
	ContentResolver mRessolver;
	PopupWindow mPopupWindow;
	CategorySelector categorySelector;
		
	final static int EXPENSE_TYPE = 0;
	final static int EARNING_TYPE = 1;
	final static int SELECTOR_ROW_NUM = 2;
	final static int SELECTOR_COLUMN_NUM = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_or_modify_item);
				
		mRessolver = getContentResolver();
		TextView returnSummary = (TextView) findViewById(R.id.returnSummary);
		returnSummary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}			
		});
		categoryTypeTitle = (TextView) 
				findViewById(R.id.categoryTypeTitle);
		titleBar = (RelativeLayout) findViewById(R.id.addItemCustomTitleBar);
		categoryTypeArray = getResources().getStringArray(R.array.category_type);
		categoryTypeTitle
		.setCompoundDrawablesWithIntrinsicBounds(0, 0, 
				android.R.drawable.arrow_down_float, 0);
		
		initSelfAccountsItem();
		initDateDetailWayMark();
		initCategoryTypeSelectPopup();
		initValueInput();
				
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.categorySelect);
		categorySelector = new CategorySelector(this);
		linearLayout.addView(categorySelector);
		categorySelector.setAdapter(new CategorySelectorAdapter());
		setCategoryType(mAccountsItem
				.getIsEarning() ? EARNING_TYPE : EXPENSE_TYPE);
		
		categoryTypeTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View source) {
				if (!mPopupWindow.isShowing()) {
					categoryTypeTitle
					.setCompoundDrawablesWithIntrinsicBounds(0, 0, 
							android.R.drawable.arrow_up_float, 0);
					mPopupWindow.showAsDropDown(titleBar);
				} else {
					mPopupWindow.dismiss();
				}
				
			}
		});		
	}
	
	@Override
	public void onActivityResult(int requestCode,
			int resultCode, Intent intent) {
		if (requestCode == StaticSettings.DETAIL_CODE
				&& resultCode == StaticSettings.DETAIL_CODE) {
			Bundle bundle = intent.getExtras();
			String detail = bundle.getString(StaticSettings.DETAIL);
			if (detail != null) {
				mAccountsItem.setDetail(detail);
			}
		}
	}
	
	private void addOrModifyItem() {		
		String category = mAccountsItem.getCategory();
		if (category == null || "".equals(category)) {
			Toast.makeText(AddOrModifyItemActivity.this,
					R.string.category_invalid_text, 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		ContentValues addItem = new ContentValues();
		int id = mAccountsItem.getId();
		
		addItem.put(StaticSettings.YEAR, mAccountsItem.getYear());
		addItem.put(StaticSettings.MONTH, mAccountsItem.getMonth());
		addItem.put(StaticSettings.DAY, mAccountsItem.getDay());
		addItem.put(StaticSettings.VALUE, mAccountsItem.getValue());
		addItem.put(StaticSettings.IS_EARNING, mAccountsItem.getIsEarning());
		addItem.put(StaticSettings.IS_MARKED, mAccountsItem.getIsMarked());
		addItem.put(StaticSettings.CATEGORY, category);
		addItem.put(StaticSettings.WAY_OF_PAYMENT, mAccountsItem.getWayOfPayment());
		addItem.put(StaticSettings.DETAIL, mAccountsItem.getDetail());
		
		Uri uri;
		if (id >= 0) {
			addItem.put(StaticSettings.ID, id);
			uri = Uri.parse("content://" + StaticSettings.AUTHORITY
					+ "/" + StaticSettings.SINGLE_ITEM + "/" + id);
			mRessolver.update(uri, addItem, null, null);
		} else {
			uri = Uri.parse("content://" + StaticSettings.AUTHORITY
					+ "/" + StaticSettings.ALL_ITEMS);
			System.out.println("Insert:" + addItem.toString());
			System.out.println("Value:" + addItem.getAsDouble(StaticSettings.VALUE));
			mRessolver.insert(uri, addItem);
		}
		finish();
	}
	
	private void initValueInput() {
		valueInput = (EditText) findViewById(R.id.valueInput);
		if (mAccountsItem.getValue() > 0.001) {
			valueInput.setText(new DecimalFormat("#.##")
			.format(mAccountsItem.getValue()));
		} else {
			valueInput.setText("0");
		}		
			            
	    if (android.os.Build.VERSION.SDK_INT <= 10) {
	    	valueInput.setInputType(InputType.TYPE_NULL);
	    } else {		
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setShowSoftInputOnFocus;
				setShowSoftInputOnFocus = cls.getMethod(
				"setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(valueInput, false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
	    }
		valueInput.requestFocus();
		
		KeyboardView valueInputKeyboard = 
				(KeyboardView) findViewById(R.id.valueInputKeyboard);
		valueInputKeyboard.setKeyboard(
				new Keyboard(this, R.xml.value_keyboard));
		valueInputKeyboard.setEnabled(true);
		valueInputKeyboard.setPreviewEnabled(false);
		valueInputKeyboard.setOnKeyboardActionListener(
				new OnKeyboardActionListener(){

					@Override
					public void onPress(int primaryCode) {						
					}

					@Override
					public void onRelease(int primaryCode) {
					}

					@Override
					public void onKey(int primaryCode, int[] keyCodes) {
						if (!valueInput.isFocused()) {
							valueInput.requestFocus();
						}
						Editable editable = valueInput.getText();
						int start = valueInput.getSelectionStart();  
						if (primaryCode == Keyboard.KEYCODE_DONE) {
							addOrModifyItem();							
						} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
							if (editable != null && start > 0) {
								if (editable.length() > 1) {
									editable.delete(start - 1, start);
								} else if (editable.length() > 0) {
									editable.clear();
									editable.append("0");
								}
							}							
						} else {							
							String stringOfInput = editable.toString();
							int pointPos = stringOfInput.indexOf(".");
							if (pointPos >= 0) {
								if (pointPos < start 
									&& editable.length() - pointPos > 2 ) {
								return;
								} else if (pointPos >= start
										&& pointPos >= 8) {
								return;
								}
							} else if (46 != primaryCode 
									&& editable.length() >= 8) {
								return;
							} else if (46 == primaryCode
									&& editable.length() > 8
									&& start == editable.length()) {
								return;
							}
														
							if ("0".equals(stringOfInput)
									&& 46 != primaryCode) {
								editable.clear();
								editable.append(Character
										.toString((char) primaryCode));
							} else {
								editable.insert(start, 
										Character.toString((char) primaryCode));
								stringOfInput = editable.toString();
								pointPos = stringOfInput.indexOf(".");
								if (pointPos >= 0
										&& editable.length() - pointPos > 3) {
									editable.delete(pointPos + 3, editable.length());
								}
							}
						}
						double value = Double
								.parseDouble(valueInput.getText().toString());
						mAccountsItem.setValue(value);
					}

					@Override
					public void onText(CharSequence text) {
					}

					@Override
					public void swipeLeft() {
					}

					@Override
					public void swipeRight() {
					}

					@Override
					public void swipeDown() {
					}

					@Override
					public void swipeUp() {
					}
					
				}); 
		
		ImageButton clearButton = (ImageButton) findViewById(R.id.valueInputClear);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable editable = valueInput.getText();
				editable.clear();
				editable.append("0");
				valueInput.requestFocus();
			}
		});
	}
	
	private void  initSelfAccountsItem() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mAccountsItem = (AccountsItem) bundle
					.get(StaticSettings.ACCOUNTS_ITEM_NAME);
		} 
		
		if (mAccountsItem == null) {
			Time time = new Time();
			time.setToNow();
			mAccountsItem = 
					new AccountsItem(time.year, time.month + 1, 
							time.monthDay, false, "",
							"", 0, false, "");
		}
		System.out.println(mAccountsItem.toString());
		
	}
				
	private void initDateDetailWayMark() {
		final TextView dateInput = (TextView) findViewById(R.id.dateInput);
		dateInput.setText(mAccountsItem.getMonth() + "月"
						+ mAccountsItem.getDay() + "日");
		dateInput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
        		final DatePickerDialog datePicker = 
        				new DatePickerDialog(AddOrModifyItemActivity.this,
        						new DatePickerDialog.OnDateSetListener() {        					
        			@Override
        			public void onDateSet(DatePicker view, 
        					int year, int monthOfYear, int dayOfMonth) {
        			}
        		}, mAccountsItem.getYear(),
        		mAccountsItem.getMonth() - 1, 
        		mAccountsItem.getDay()) {
				    @Override
				    protected void onCreate(Bundle savedInstanceState) {
				    	super.onCreate(savedInstanceState);
				    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				    }
        			
        		    public void onDateChanged(DatePicker view, int year,
        		            int month, int day) {
        		    	super.onDateChanged(view, year, month, day);
        				String[] dayOfWeekArray = getResources()
        						.getStringArray(R.array.day_of_week);
        				Calendar calendar = Calendar.getInstance();
        				calendar.set(year, month, day);
        				String dayOfWeekText = 
        						dayOfWeekArray[calendar
        						               .get(Calendar.DAY_OF_WEEK) - 1];
        				setTitle(year + "年" + (month + 1) + "月"
        								+ day + "日" + dayOfWeekText);
        		    }
        		};
        		datePicker.setCancelable(true);
        		datePicker.setCanceledOnTouchOutside(true);
        		datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, 
        				getText(R.string.cancel_text),
        				new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				dialog.cancel();
        			}
        		});
        		datePicker.setButton(DialogInterface.BUTTON_POSITIVE,
        				getText(R.string.confirm_text),
        				new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				DatePicker datePicker = ((DatePickerDialog)dialog)
        						.getDatePicker();
        				mAccountsItem.setYear(datePicker.getYear());
        				mAccountsItem.setMonth(datePicker.getMonth() + 1);
        				mAccountsItem.setDay(datePicker.getDayOfMonth());
        				dateInput.setText(mAccountsItem.getMonth() + "月"
        								+ mAccountsItem.getDay() + "日");
        			}
        		});
        		datePicker.show();
			}
		});
		
		TextView detailInput = (TextView) findViewById(R.id.detailInput);
		detailInput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddOrModifyItemActivity.this,
						DetailInputActivity.class);
				intent.putExtra(StaticSettings
						.ACCOUNTS_ITEM_NAME, mAccountsItem);
				startActivityForResult(intent, StaticSettings.DETAIL_CODE);
			}			
		});
		
		final TextView wayInput = (TextView) findViewById(R.id.wayInput);
		final String[] wayOfPaymentList = 
				getResources().getStringArray(R.array.way_of_payment_list);
		String wayOfPayment = mAccountsItem.getWayOfPayment();
		if (wayOfPayment == null 
				|| "".equals(wayOfPayment)) {
			mAccountsItem.setWayOfPayment(wayOfPaymentList[0]);
		}
		wayInput.setText(mAccountsItem.getWayOfPayment());
		wayInput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = 
						new AlertDialog.Builder(AddOrModifyItemActivity.this);
				builder.setTitle(getText(R.string.way_of_payment_text));
				builder.setItems(wayOfPaymentList, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								mAccountsItem.setWayOfPayment(
										wayOfPaymentList[which]);
								wayInput.setText(mAccountsItem
										.getWayOfPayment());
							}});
				builder.create().show();
			}
		});

		final CheckBox markInput = (CheckBox) findViewById(R.id.markInput);
		if (mAccountsItem.getIsMarked()) {
			markInput.setChecked(true);
		} else {
			markInput.setChecked(false);
		}
		markInput.setOnCheckedChangeListener(
				new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						mAccountsItem.setIsMarked(isChecked);
					}
				});		
	}
	
	private void setCategoryType(int position) {
		System.out.println("setCategoryType");
		
		if (currentCategoryType == position) {
			return;
		}
		
		categoryTypeTitle.setText(categoryTypeArray[position]);
		currentCategoryType = position;
		boolean isEarning = (position == EARNING_TYPE);
				
		categorySelector.reloadDataSet();
		categorySelector.setOnItemClickListener(
				new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				CategorySelectorAdapter adapter = 
						((CategorySelectorAdapter) categorySelector
						.getAdapter());
				String itemName = (String) adapter.getItem(position);
				if (StaticSettings.ADD_CATEGORY_NAME.equals(itemName)) {
					AlertDialog addDialog = new AlertDialog
							.Builder(AddOrModifyItemActivity
									.this).create();
					addDialog.setCancelable(true);
					addDialog.setCanceledOnTouchOutside(true);
					addDialog.setTitle(R.string.add_category_dailog_title);
					
					final EditText newCategoryEdit = 
							new EditText(addDialog.getContext());
					newCategoryEdit.setHint(R.string.add_category_hint);
					newCategoryEdit.setLayoutParams(
							new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT, 
									LinearLayout.LayoutParams.WRAP_CONTENT));
					newCategoryEdit.setSingleLine(true);
					newCategoryEdit.setFilters(
							new InputFilter[]{new InputFilter.LengthFilter(4)});
					
					addDialog.setView(newCategoryEdit);
					addDialog.setButton(DialogInterface.BUTTON_NEGATIVE, 
	        				getText(R.string.cancel_text),
	        				new DialogInterface.OnClickListener() {
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				dialog.cancel();
	        			}
	        		});
					addDialog.setButton(DialogInterface.BUTTON_POSITIVE,
	        				getText(R.string.confirm_text),
	        				new DialogInterface.OnClickListener() {
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				String newCategoryName = newCategoryEdit
	        						.getText().toString().trim();
	        				dialog.dismiss();
	        				if (!"".equals(newCategoryName)) {
	        					CustomTypesEditor customTypesEditor = 
	        							new CustomTypesEditor(AddOrModifyItemActivity.this);
	        					if (currentCategoryType == EARNING_TYPE) {
	        						if (customTypesEditor
	        								.isEarningTypeExist(newCategoryName)) {
	        							Toast.makeText(AddOrModifyItemActivity.this,
	        									R.string.add_category_duplicate_warning, 
	        									Toast.LENGTH_SHORT).show();
	        						} else {
	        							customTypesEditor.addEarningType(newCategoryName);
	        							categorySelector.reloadDataSet();
	        							categorySelector
	        							.setSelectedItemToNewest();
	        							mAccountsItem.setCategory(
	        									categorySelector.getSelectedCategory());
	        						}
	        					} else {
	        						if (customTypesEditor
	        								.isExpenseTypeExist(newCategoryName)) {
	        							Toast.makeText(AddOrModifyItemActivity.this,
	        									R.string.add_category_duplicate_warning, 
	        									Toast.LENGTH_LONG).show();
	        						} else {
	        							customTypesEditor.addExpenseType(newCategoryName);
	        							categorySelector.reloadDataSet();
	        							categorySelector
	        							.setSelectedItemToNewest();
	        							mAccountsItem.setCategory(
	        									categorySelector.getSelectedCategory());
	        						}
	        					}
	        				} else {
    							Toast.makeText(AddOrModifyItemActivity.this,
    									R.string.add_category_block_warning, 
    									Toast.LENGTH_SHORT).show();
	        				}
	        			}
	        		});
					addDialog.show();
				} else {
					categorySelector
					.setSelectedItemOnDisplay(position);	
					mAccountsItem.setCategory(
							categorySelector.getSelectedCategory());
				}
			}
		});
		categorySelector.setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						CategorySelectorAdapter adapter = 
								((CategorySelectorAdapter) categorySelector
								.getAdapter());
						int currentPage = adapter.getCurrentPageNo();
						final String itemName = (String) adapter.getItem(position);
						if (currentPage < adapter.getCustomPageNum()
								&& (!StaticSettings
										.ADD_CATEGORY_NAME.equals(itemName))) {
							AlertDialog.Builder builder = 
									new AlertDialog.Builder(AddOrModifyItemActivity.this);
							builder.setTitle(itemName);
							final CharSequence editItemText = 
									getText(R.string.modify_category_edit);
							final CharSequence deleteItemText = 
									getText(R.string.modify_category_delete);
							final CharSequence []itemsList = 
									new CharSequence[] {editItemText, deleteItemText};
							builder.setItems(itemsList, 
									new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									if (editItemText.equals(itemsList[which])) {
										AlertDialog editDialog = new AlertDialog
												.Builder(AddOrModifyItemActivity
														.this).create();
										editDialog.setCancelable(true);
										editDialog.setCanceledOnTouchOutside(true);
										editDialog.setTitle(R.string.modify_category_edit_title);
										final EditText categoryEdit = 
												new EditText(editDialog.getContext());
										categoryEdit.setLayoutParams(
												new LinearLayout.LayoutParams(
														LinearLayout.LayoutParams.MATCH_PARENT, 
														LinearLayout.LayoutParams.WRAP_CONTENT));
										categoryEdit.setSingleLine(true);
										categoryEdit.setFilters(
												new InputFilter[]{new InputFilter.LengthFilter(4)});
										categoryEdit.setText(itemName);
										
										editDialog.setView(categoryEdit);
										editDialog.setButton(DialogInterface.BUTTON_NEGATIVE, 
						        				getText(R.string.cancel_text),
						        				new DialogInterface.OnClickListener() {
						        			@Override
						        			public void onClick(DialogInterface dialog, int which) {
						        				dialog.cancel();
						        			}
						        		});
										editDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						        				getText(R.string.confirm_text),
						        				new DialogInterface.OnClickListener() {
						        			@Override
						        			public void onClick(DialogInterface dialog, int which) {
						        				String newCategoryName = categoryEdit
						        						.getText().toString().trim();
						        				dialog.dismiss();
						        				if (newCategoryName.equals(itemName)) {
						        					return;
						        				}
						        				if (!"".equals(newCategoryName)) {
						        					CustomTypesEditor customTypesEditor = 
						        							new CustomTypesEditor(AddOrModifyItemActivity.this);
						        					if (currentCategoryType == EARNING_TYPE) {
						        						if (customTypesEditor
						        								.isEarningTypeExist(newCategoryName)) {
						        							Toast.makeText(AddOrModifyItemActivity.this,
						        									R.string.add_category_duplicate_warning, 
						        									Toast.LENGTH_SHORT).show();
						        						} else {
						        							customTypesEditor
						        							.editEarningType(itemName, newCategoryName);
						        							categorySelector.reloadDataSet();
						        						}
						        					} else {
						        						if (customTypesEditor
						        								.isExpenseTypeExist(newCategoryName)) {
						        							Toast.makeText(AddOrModifyItemActivity.this,
						        									R.string.add_category_duplicate_warning,
						        									Toast.LENGTH_LONG).show();
						        						} else {
						        							customTypesEditor
						        							.editExpenseType(itemName, newCategoryName);
						        							categorySelector.reloadDataSet();
						        						}
						        					}
						        				} else {
					    							Toast.makeText(AddOrModifyItemActivity.this,
					    									R.string.add_category_block_warning, 
					    									Toast.LENGTH_SHORT).show();
						        				}
						        			}
						        		});
										editDialog.show();
									} else if (deleteItemText.equals(itemsList[which])) {
										AlertDialog deleteDialog = new AlertDialog
												.Builder(AddOrModifyItemActivity
														.this).create();
										final boolean isEarning = 
												currentCategoryType == EARNING_TYPE;
										int isEarningValue = isEarning ? 1 : 0;
										deleteDialog.setCancelable(true);
										deleteDialog.setCanceledOnTouchOutside(true);
										ContentResolver reslover = getContentResolver();
										Uri queryUri = Uri.parse("content://" + StaticSettings.AUTHORITY
												+ "/" + StaticSettings.ALL_ITEMS);	
										Cursor cursor = reslover.query(queryUri, null, 
												StaticSettings.IS_EARNING + "=? AND "
												+ StaticSettings.CATEGORY + "=?", 
												new String[]{isEarningValue + "",itemName + "" }, null);
										CharSequence deleteConfirmText;
										if (cursor.getCount() > 0) {
											deleteConfirmText = 
													getText(R.string.modify_category_delete_warning);
										} else {
											deleteConfirmText = 
													getText(R.string.modify_category_delete_confirm);
										}
										deleteDialog.setMessage(deleteConfirmText);
										deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, 
						        				getText(R.string.cancel_text),
						        				new DialogInterface.OnClickListener() {
						        			@Override
						        			public void onClick(DialogInterface dialog, int which) {
						        				dialog.cancel();
						        			}
						        		});
										deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE,
						        				getText(R.string.confirm_text),
						        				new DialogInterface.OnClickListener() {
						        			@Override
						        			public void onClick(DialogInterface dialog, int which) {
						        				dialog.dismiss();
						        				String oldSelectedItem = 
						        						categorySelector.getSelectedCategory();
						        				CustomTypesEditor customTypesEditor = 
					        							new CustomTypesEditor(AddOrModifyItemActivity.this);
					        					if (isEarning) {
					        						customTypesEditor.removeEarningType(itemName);
					        					} else {
					        						customTypesEditor.removeExpenseType(itemName);
					        					}
					        					categorySelector.reloadDataSet();
					        					categorySelector.setSelectedItem(
					        							categorySelector.getCategoryId(oldSelectedItem));
			        							mAccountsItem.setCategory(
			        									categorySelector.getSelectedCategory());
						        			}
						        		});
										deleteDialog.show();
									}
								}
							});
							builder.create().show();
						}
						
						return false;
					}
					
				});
		CategorySelectorAdapter categoryAdapter = 
				((CategorySelectorAdapter) categorySelector
				.getAdapter());
		String categoryName = mAccountsItem.getCategory();
		if (categoryName != null && !"".equals(categoryName)
				&& isEarning == mAccountsItem.getIsEarning()) {
			int categoryId = categoryAdapter.getCategoryId(categoryName);
			categorySelector.setSelectedItem(categoryId);
			categorySelector.setPrimaryToSelectedPage();
		} else {
			categorySelector.setSelectedItem(
					categoryAdapter.getCustomPageNum(), 0);
			categorySelector.setPrimaryToSelectedPage();
			mAccountsItem.setCategory(
					categorySelector.getSelectedCategory());
		}
		mAccountsItem.setIsEarning(isEarning);
	}
			
	private void initCategoryTypeSelectPopup() {
		LayoutParams layoutParams 
			= new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		final ListView popupList = new ListView(this);
		popupList.setLayoutParams(layoutParams);
				
		BaseAdapter adapter = new MyListAdapter();
		popupList.setAdapter(adapter);
		popupList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyListAdapter listAdapter = 
						(MyListAdapter) popupList.getAdapter();
				setCategoryType(position);
				listAdapter.notifyDataSetChanged();	
				
				final Handler handler = new Handler();
			    Runnable runnable = new Runnable() {
			      @Override
			      public void run() {
			        if (mPopupWindow.isShowing()) {
			        	mPopupWindow.dismiss();
			        	handler.removeCallbacks(this);
			        }
			      }
			    };
			    handler.postDelayed(runnable, 300);
			}
		});
		
		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(layoutParams);
		linearLayout.setFocusable(true);
		linearLayout.addView(popupList);
		
		linearLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mPopupWindow.dismiss();
				return false;
			}
		});
		
		mPopupWindow = 
				new PopupWindow(linearLayout, 
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		mPopupWindow.setBackgroundDrawable(
				new ColorDrawable(Color.argb(50, 0, 0, 0)));
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				categoryTypeTitle
				.setCompoundDrawablesWithIntrinsicBounds(0, 0, 
						android.R.drawable.arrow_down_float, 0);
			}
		});
	}
	
	private class MyListAdapter extends BaseAdapter  {
		
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object getItem(int position) {
			return categoryTypeArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater
						.from(AddOrModifyItemActivity.this);
				convertView = inflater
						.inflate(R.layout
								.category_type_select_item, null);
			}
			
			ImageView categoryTypeIcon = (ImageView) convertView
					.findViewById(R.id.categoryTypeIcon);
			ImageView isSelected = (ImageView) convertView
					.findViewById(R.id.isSelected);
			TextView categoryTypeTextView = (TextView) convertView
					.findViewById(R.id.categoryTypeText);
			if (position == EXPENSE_TYPE) {
				categoryTypeIcon.setImageResource(R.drawable.expense_icon);
				categoryTypeTextView.setText(categoryTypeArray[EXPENSE_TYPE]);
			} else {
				categoryTypeIcon.setImageResource(R.drawable.earning_icon);
				categoryTypeTextView.setText(categoryTypeArray[EARNING_TYPE]);
			}
			if (currentCategoryType == position) {
				isSelected.setVisibility(View.VISIBLE);
			} else {
				isSelected.setVisibility(View.INVISIBLE);
			}
				
			return convertView;
		}}
	
	public class CategorySelector extends HorizontalScrollView {
		
		private int pageWidth;
		private int primaryPageNo = -1;
		
		private ArrayList<GridView> mGridList = 
				new ArrayList<GridView>();	
		OnItemClickListener mOnItemClickListener;
		OnItemLongClickListener mOnItemLongClickListener;
		
		
		private CategorySelectorAdapter mAdapter;
		private ArrayList<CategorySelectorAdapter> mAdapterList = 
				new ArrayList<CategorySelectorAdapter>();
		
		public CategorySelector(Context context) {
			super(context);
			initSelf();
		}

		public CategorySelector(Context context, AttributeSet attrs) {
			super(context, attrs);
			initSelf();
		}

		public CategorySelector(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initSelf();
		}
		
		private void initSelf() {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			pageWidth = metrics.widthPixels;
			LinearLayout.LayoutParams params = 
					new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			setLayoutParams(params);
			setHorizontalScrollBarEnabled(false);
		}
		
	    @Override
	    public boolean onTouchEvent(MotionEvent ev) {
	    	boolean actionRes = super.onTouchEvent(ev);
	    	if ((ev.getAction() & 
	    			MotionEvent.ACTION_MASK) 
	    			== MotionEvent.ACTION_UP) {
	    		smoothScrollToPage();
	    	}   	
	    	return actionRes;
	    }
	    
	    @Override
	    protected void onLayout(boolean changed, int l, int t, int r, int b) { 
	    	super.onLayout(changed, l, t, r, b);
	    	if (primaryPageNo > 0
	    			&& primaryPageNo < mAdapter.getPageCount()) {
				mAdapter.setCurrentPageNo(primaryPageNo);
				scrollTo(primaryPageNo * pageWidth , getScrollY());
				primaryPageNo = -1;
	    	}
	    }
	    
	    public void setPrimaryToSelectedPage() {
	    	int pageRowNum = mAdapter.getPageRowNum();
	    	int pageColumnNum = mAdapter.getPageColumnNum();
	    	int customTypeNum = mAdapter.getCustomTypeNum();
	    	int presetTypeNum = mAdapter.getPresetTypeNum();
	    	int pageNo;
	    	int selectedItemId = mAdapter.getSelectedItemId();
	    	if (selectedItemId >= 0 && 
	    			selectedItemId < customTypeNum) {
	    		pageNo = selectedItemId / (pageRowNum * pageColumnNum);
	    	} else if (selectedItemId >= customTypeNum &&
	    			selectedItemId < customTypeNum + presetTypeNum) {
	    		pageNo = (selectedItemId - customTypeNum) 
	    				/ (pageRowNum * pageColumnNum)
	    				+ mAdapter.getCustomPageNum();
	    	} else {
	    		pageNo = mAdapter.getCustomPageNum();
	    	}
	    	setPrimaryPage(pageNo);
	    }
	    
	    private void setPrimaryPage(int primaryPageNo) {
	    	this.primaryPageNo = primaryPageNo;
	    }
	    
		private void smoothScrollToPage() {
			int pageColumnNum = mAdapter.getPageColumnNum();
			int oldPageNo = mAdapter.getCurrentPageNo();
			int currentX = getScrollX();
			if (currentX < oldPageNo * pageWidth 
					- (pageWidth / pageColumnNum / 2)) {
				mAdapter.pageUp();
			} else if (currentX > oldPageNo * pageWidth 
					+ (pageWidth / pageColumnNum / 2)) {
				mAdapter.pageDown();
			} 				
			smoothScrollTo(mAdapter.getCurrentPageNo() 
					* pageWidth , getScrollY());
		}
						
		public void setAdapter(CategorySelectorAdapter adapter) {
			mAdapter = adapter;
			reloadDataSet();
		}
		
		private void createAdapterList() {
			mAdapterList.clear();
			int pageCount = mAdapter.getPageCount();
			for (int i = 0; i < pageCount; i++) {
				CategorySelectorAdapter adapter = mAdapter.getCopy();
				adapter.setCurrentPageNo(i);
				mAdapterList.add(adapter);
			}
		}
		
		public void setSelectedItemToNewest() {
			CategorySelectorAdapter firstPageAdapter = mAdapterList.get(0);
			String firstOfCustomPage = 
					(String) firstPageAdapter.getItem(0);
			if (StaticSettings.ADD_CATEGORY_NAME
					.equals(firstOfCustomPage)) {
				setSelectedItem(0, 1);
			} else {
				setSelectedItem(0, 0);
			}
		}
		
		public void setSelectedItemOnDisplay(int position) {
			int currentPageNo = mAdapter.getCurrentPageNo();
			setSelectedItem(currentPageNo, position);
		}
		
		public void setSelectedItem(int currentPageNo, int position) {
			int pageCount = mAdapter.getPageCount();
			mAdapter.setSelectedItem(currentPageNo, position);
			for (int i = 0; i < pageCount; i++) {
				mAdapterList.get(i)
				.setSelectedItem(currentPageNo, position);
			}
		}
		
		public void setSelectedItem(int itemId) {
			int pageCount = mAdapter.getPageCount();
			mAdapter.setSelectedItem(itemId);
			for (int i = 0; i < pageCount; i++) {
				mAdapterList.get(i)
				.setSelectedItem(itemId);
			}
		}
		
		public int getSelectedItemId() {
			return mAdapter.getSelectedItemId();
		}
		
		public String getSelectedCategory() {
			return mAdapter.getSelectedCategory();
		}
		
		public int getCategoryId(String categoryName) {
			return mAdapter.getCategoryId(categoryName);
		}
		
	    public void setOnItemClickListener(OnItemClickListener listener) {
	        mOnItemClickListener = listener;
	        int pageCount = mAdapter.getPageCount();
	        for (int i = 0; i < pageCount; i++) {
	        	mGridList.get(i)
	        	.setOnItemClickListener(mOnItemClickListener);
	        }
	    }
		
	    public void setOnItemLongClickListener(OnItemLongClickListener listener) {

	        mOnItemLongClickListener = listener;
	        int customPageCount = mAdapter.getCustomPageNum();
	        for (int i = 0; i < customPageCount; i++) {
		    	GridView gridView = mGridList.get(i);
		        if (!gridView.isLongClickable()) {
		        	gridView.setLongClickable(true);
		        }
		        gridView.setOnItemLongClickListener(mOnItemLongClickListener);
	        }
	        	        
	    }
		
		public CategorySelectorAdapter getAdapter() {
			return mAdapter;
		}
				
		private void reloadDataSet() {
			mAdapter.reload();
			createAdapterList();
			removeAllViews();
			mGridList.clear();
			int pageCount = mAdapter.getPageCount();
			int pageColumnNum = mAdapter.getPageColumnNum();
			LinearLayout layout = new LinearLayout(getContext());
			int layoutWidth = pageWidth * pageCount;
			LinearLayout.LayoutParams params = 
					new LinearLayout.LayoutParams(
					layoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.setLayoutParams(params);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < pageCount; i++) {
				GridView gridView = new GridView(getContext());
				LinearLayout.LayoutParams gridParams = 
						new LinearLayout.LayoutParams(pageWidth, 
								LinearLayout.LayoutParams.WRAP_CONTENT);
				gridView.setLayoutParams(gridParams);
				gridView.setColumnWidth(pageWidth / pageColumnNum);
				gridView.setStretchMode(GridView.NO_STRETCH);
				gridView.setNumColumns(pageColumnNum);			
				gridView.setAdapter(mAdapterList.get(i));
				mGridList.add(gridView);
				layout.addView(mGridList.get(i));
			}
			addView(layout);
			if (mOnItemClickListener != null) {
				setOnItemClickListener(mOnItemClickListener);
			}
			if (mOnItemLongClickListener != null) {
				setOnItemLongClickListener(mOnItemLongClickListener);
			}
			System.out.println("reloadDataSet");
		}
		
	}
	
	private class CategorySelectorAdapter extends BaseAdapter {
		
		private int customPageNum = 1;
		private int customLimitNum = SELECTOR_ROW_NUM * SELECTOR_COLUMN_NUM;
		private int pageCount = 2;
		private int currentPageNo = 0;
		private int pageRowNum = SELECTOR_ROW_NUM;
		private int pageColumnNum = SELECTOR_COLUMN_NUM;
		private int selectCategoryId = -1;
			
		private ArrayList<String> mCustomCategoryList = 
				new ArrayList<String>();
		private ArrayList<String> mPresetCategoryList = 
				new ArrayList<String>();

		@Override
		public int getCount() {
			int itemCount;
			if (currentPageNo == (customPageNum - 1)) {
				itemCount = mCustomCategoryList.size() - 
						(customPageNum - 1) * pageRowNum * pageColumnNum;
			} else if (currentPageNo == pageCount - 1) {
				itemCount = mPresetCategoryList.size() - 
						(pageCount - customPageNum - 1) 
						* pageRowNum * pageColumnNum;
			} else {
				itemCount = pageRowNum * pageColumnNum;
			}
			return itemCount;
		}

		@Override
		public Object getItem(int position) {
			int itemId = (int) getItemId(position);
			if (itemId < mCustomCategoryList.size()) {
				return mCustomCategoryList.get(itemId);
			} else {
				itemId = itemId - mCustomCategoryList.size();
				return mPresetCategoryList.get(itemId);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater
					.from(AddOrModifyItemActivity.this);
			convertView = inflater
					.inflate(R.layout
							.category_select_item, null);
			
			boolean isEarning = currentCategoryType == EARNING_TYPE;
			CustomTypesEditor customTypesEditor = 
					new CustomTypesEditor(AddOrModifyItemActivity.this);
			String categoryName = (String) getItem(position);
			Drawable categoryIcon = customTypesEditor
					.getCategoryDrawabe(isEarning, categoryName);
			
			ImageView categoryImage = (ImageView) convertView
					.findViewById(R.id.categoryImage);
			TextView categoryNameTv = (TextView) convertView
					.findViewById(R.id.categoryName);
			
			categoryImage.setImageDrawable(categoryIcon);
			categoryNameTv.setText(categoryName);
			
			if (selectCategoryId == 
					(int) getItemId(position)) {
				if (isEarning) {
					categoryImage.setBackground(getResources()
							.getDrawable(R.color.earning_color));
				} else {
					categoryImage.setBackground(getResources()
							.getDrawable(R.color.expense_color));
				}
			}
			
			return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			if (currentPageNo < customPageNum) {
				return currentPageNo * pageRowNum 
						* pageColumnNum + position;
			} else {
				return (currentPageNo - customPageNum)
						* pageRowNum * pageColumnNum 
						+ mCustomCategoryList.size() + position;
			}
		}
		
		public CategorySelectorAdapter getCopy() {
			CategorySelectorAdapter newObject = 
					new CategorySelectorAdapter();
			
			ArrayList<String> customCategoryList = 
					new ArrayList<String>();
			int customCount = mCustomCategoryList.size();
			for (int i = 0; i < customCount; i++) {
				String categoryName = mCustomCategoryList.get(i);
				if (StaticSettings.ADD_CATEGORY_NAME.equals(categoryName)) {
					customCategoryList.add(categoryName);
				}
			}
			newObject.reload(customCategoryList, mPresetCategoryList);
			newObject.setCustomLimitNum(customLimitNum);
			newObject.setPageRowAndColumnNum(pageRowNum, pageColumnNum);
			newObject.setCurrentPageNo(currentPageNo);
			newObject.setSelectedItem(selectCategoryId);
			
			return newObject;
		}
		
		public void setSelectedItem(int pageNo, int position) {
			int itemId;
			if (pageNo < customPageNum) {
				itemId = pageNo * pageRowNum 
						* pageColumnNum + position;
			} else {
				itemId = (pageNo - customPageNum)
						* pageRowNum * pageColumnNum 
						+ mCustomCategoryList.size() + position;
			}
			setSelectedItem(itemId);
		}
		
		public void setSelectedItem(int itemId) {
			selectCategoryId = itemId;
			notifyDataSetChanged();
		}
		
		public int getSelectedItemId() {
			return selectCategoryId;
		}
		
		public String getSelectedCategory() {
			int customNum = mCustomCategoryList.size();
			int presetNum = mPresetCategoryList.size();
			if (selectCategoryId < 0 ||
					selectCategoryId >= 
					customNum + presetNum) {
				return null;
			}
			if (selectCategoryId < customNum) {
				return (String) mCustomCategoryList
						.get(selectCategoryId);
			} else {
				return (String) mPresetCategoryList
						.get(selectCategoryId - customNum);
			}
		}
		
		public int getCategoryId(String categoryName) {
			int customNum = mCustomCategoryList.size();
			int presetNum = mPresetCategoryList.size();
			for (int i = 0; i < customNum; i++) {
				if (categoryName
						.equals(mCustomCategoryList.get(i))) {
					return i;
				}
			}
			for (int j = 0; j < presetNum; j++) {
				if (categoryName
						.equals(mPresetCategoryList.get(j))) {
					return j + customNum;
				}
			}
			return -1;
		}
		
		public int getCustomTypeNum() {
			return mCustomCategoryList.size();
		}
		
		public int getPresetTypeNum() {
			return mPresetCategoryList.size();
		}
		
		public int getCustomPageNum() {
			return customPageNum;
		}
		
		public int getPageCount() {
			return pageCount;
		}
		
		public int getCurrentPageNo() {
			return currentPageNo;
		}
		
		public void setCurrentPageNo(int currentPageNo) {
			if (currentPageNo >= 0 &&
					currentPageNo < pageCount) {
				this.currentPageNo = currentPageNo;
			}
		}
		
		public void setPageRowNum(int pageRowNum) {
			this.pageRowNum = pageRowNum;
			reCount();
		}
		
		public int getPageRowNum() {
			return pageRowNum;
		}
		
		public void setPageColumnNum(int pageColumnNum) {
			this.pageColumnNum = pageColumnNum;
			reCount();
		}
		
		public int getPageColumnNum() {
			return pageColumnNum;
		}
		
		public void pageDown() {
			if (currentPageNo < pageCount - 1) {
				currentPageNo++;
				notifyDataSetChanged();
			}
		}
		
		public void pageUp() {
			if (currentPageNo > 0) {
				currentPageNo--;
				notifyDataSetChanged();
			}
		}
		
		public void setPageRowAndColumnNum(int pageRowNum, 
				int pageColumnNum) {
			this.pageRowNum = pageRowNum;
			this.pageColumnNum = pageColumnNum;
			reCount();
		}
		
		public void setCustomLimitNum(int customLimitNum) {
			if (mCustomCategoryList.size() > customLimitNum) {
				return;
			}
			this.customLimitNum = customLimitNum;
		}
		
		private void reCount() {
			customPageNum = 
					(mCustomCategoryList.size() - 1) / (pageRowNum * pageColumnNum) + 1;
			int presetCategoryPageNum = 
					(mPresetCategoryList.size() - 1) / (pageRowNum * pageColumnNum) + 1;
			pageCount = presetCategoryPageNum + customPageNum;
			notifyDataSetChanged();
		}
		
		private void printCategoryList() {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("mCustomCategoryList:");
			for (int i = 0;  i < mCustomCategoryList.size(); i++) {
				stringBuffer.append(mCustomCategoryList.get(i) + ",");
			}
			stringBuffer.append("\n");
			stringBuffer.append("mPresetCategoryList:");
			for (int j = 0; j < mPresetCategoryList.size(); j++) {
				stringBuffer.append(mPresetCategoryList.get(j) + ",");
			}
			System.out.println(stringBuffer.toString());
		}
		
		public void reload(ArrayList<String> customCategoryList, 
				ArrayList<String> presetCategoryList) {
			mCustomCategoryList.clear();
			int customCategoryPageNum;
			if (customCategoryList.size() < customLimitNum) {
				customCategoryPageNum = 
						customCategoryList.size() / (pageRowNum * pageColumnNum) + 1;
				mCustomCategoryList.add(StaticSettings.ADD_CATEGORY_NAME);
			} else {
				customCategoryPageNum = 
						(customLimitNum - 1) / (pageRowNum * pageColumnNum) + 1;
			}
			for (int i = 0; i < customCategoryList.size(); i++) {
				mCustomCategoryList.add(customCategoryList.get(i));
			}
			customPageNum = customCategoryPageNum;
			
			mPresetCategoryList.clear();
			for (int j = 0; j < presetCategoryList.size(); j++) {
				mPresetCategoryList.add(presetCategoryList.get(j));
			}
			int presetCategoryPageNum = 
					(mPresetCategoryList.size() - 1) / (pageRowNum * pageColumnNum) + 1;
			
			pageCount = presetCategoryPageNum + customPageNum;
//			printCategoryList();
			
		}
						
		public void reload(ArrayList<String> customCategoryList, 
				String[] presetCategoryList) {
			ArrayList<String> presetCategoryArrayList = 
					new ArrayList<String>();
			for (int i = 0; i < presetCategoryList.length; i++) {
				presetCategoryArrayList
				.add(presetCategoryList[i]);
			}
			reload(customCategoryList, presetCategoryArrayList);
		}
		
		public void reload() {
			CustomTypesEditor customTypesEditor = 
					new CustomTypesEditor(AddOrModifyItemActivity.this);

			ArrayList<String> customCategoryList;
			String[] presetCategoryList;
			
			if (currentCategoryType == EARNING_TYPE) {
				customCategoryList = 
						customTypesEditor.getCustomEarningTypes();
				presetCategoryList = AddOrModifyItemActivity.this
						.getResources().getStringArray(R.array.preset_earning_types);
			} else {
				customCategoryList = 
						customTypesEditor.getCustomExpenseTypes();
				presetCategoryList = AddOrModifyItemActivity.this
						.getResources().getStringArray(R.array.preset_expense_types);
			}
			reload(customCategoryList, presetCategoryList);
		}
		
	}
}
