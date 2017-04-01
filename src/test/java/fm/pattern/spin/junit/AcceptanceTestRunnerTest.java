package fm.pattern.spin.junit;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fm.pattern.spin.config.SpinConfigurationTest;
import fm.pattern.spin.junit.AcceptanceTestRunner;
import fm.pattern.spin.junit.TestExecutionMonitor;

public class AcceptanceTestRunnerTest {

    @Mock
    private RunNotifier notifier;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TestExecutionMonitor.reset();
    }

    @Test
    public void shouldIncrementTheNumberOfTestClassesToRun() throws Exception {
        Assertions.assertThat(TestExecutionMonitor.getTestClassesToRun()).isEqualTo(0);

        new AcceptanceTestRunner(TestExecutionMonitorTest.class);
        Assertions.assertThat(TestExecutionMonitor.getTestClassesToRun()).isEqualTo(1);

        new AcceptanceTestRunner(SpinConfigurationTest.class);
        Assertions.assertThat(TestExecutionMonitor.getTestClassesToRun()).isEqualTo(2);
    }

    @Test
    public void shouldBeAbleToAddANewRunListenerWhenTheRunnerIsRun() throws Exception {
        AcceptanceTestRunner runner = new AcceptanceTestRunner(SpinConfigurationTest.class);
        runner.run(notifier);

        Mockito.verify(notifier, Mockito.times(1)).addListener((RunListener) Mockito.anyObject());
    }

}
