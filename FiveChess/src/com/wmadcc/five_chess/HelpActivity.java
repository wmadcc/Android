package com.wmadcc.five_chess;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends Activity {
	
	final String FILE_NAME = "help_file.bin";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		TextView helpTextView = (TextView) 
				findViewById(R.id.helpTextView);
		String helpText = readFromHelpFile();
		helpTextView.setText(helpText);
	}

	private String readFromHelpFile() {
		try {
			InputStream helpFile = getAssets().open(FILE_NAME);
			byte[] buff = new byte[1024];
			int hasRead = 0;
			StringBuilder sb = new StringBuilder("");
			while((hasRead = helpFile.read(buff)) > 0) {
				sb.append(new String(buff, 0, hasRead));
			}
			helpFile.close();
			return sb.toString();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
