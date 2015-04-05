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
		solo.clickOnButton("规则说明");
		solo.assertCurrentActivity("Expected HelpActivity Activity",
				"HelpActivity");
		solo.takeScreenshot();
		solo.goBack();
		solo.takeScreenshot();
	}
	
	public void testOpenChooseTurnActivity() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("开始游戏");
		solo.assertCurrentActivity("Excepted ChooseTurnActivity Activity",
				"ChooseTurnActivity");
		solo.takeScreenshot();
		solo.goBack();
		solo.takeScreenshot();
	}

	public void testGoBackButton() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("开始游戏");
		solo.takeScreenshot();
		solo.clickOnButton("返回");
		solo.takeScreenshot();		
	}
	
	public void testExitButton() throws Exception {
		solo.takeScreenshot();
		solo.clickOnButton("退出");
		solo.takeScreenshot();
	}
	
}
