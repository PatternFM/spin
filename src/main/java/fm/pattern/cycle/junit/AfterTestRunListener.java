package fm.pattern.cycle.junit;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import fm.pattern.cycle.AutomatedAcceptanceTest;

public class AfterTestRunListener extends RunListener {

	public void testRunFinished(Result result) throws Exception {
		TestExecutionMonitor.testCompleted();
		if (TestExecutionMonitor.allTestsHaveRun()) {
			AutomatedAcceptanceTest.stop();
		}
		super.testRunFinished(result);
	}

}