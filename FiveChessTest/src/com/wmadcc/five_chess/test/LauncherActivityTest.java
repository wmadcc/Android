package com.wmadcc.five_chess.test;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.wmadcc.five_chess.StartActivity;

public class LauncherActivityTest extends
		ActivityInstrumentationTestCase2<StartActivity> {
	
	private Solo solo;
	public LauncherActivityTest() {
		super(StartActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), 
				getActivity());
	}
	
	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
	
	public void testOpenHelp() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("����˵��");
		solo.assertCurrentActivity("Expected HelpActivity Activity",
				"HelpActivity");
		solo.takeScreenshot();
		solo.goBack();
		solo.takeScreenshot();
	}
	
	public void testOpenChooseTurnActivity() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("��ʼ��Ϸ");
		solo.assertCurrentActivity("Excepted ChooseTurnActivity Activity",
				"ChooseTurnActivity");
		solo.takeScreenshot();
		solo.goBack();
		solo.takeScreenshot();
	}

	public void testGoBackButton() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("��ʼ��Ϸ");
		solo.takeScreenshot();
		solo.clickOnButton("����");
		solo.takeScreenshot();		
	}
	
	public void testExitButton() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("�˳�");
		solo.takeScreenshot();
	}
	
}
