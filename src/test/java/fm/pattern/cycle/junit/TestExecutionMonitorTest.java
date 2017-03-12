package fm.pattern.cycle.junit;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import fm.pattern.cycle.PatternAssertions;

public class TestExecutionMonitorTest {

    @Before
    public void before() {
        TestExecutionMonitor.reset();
    }

    @Test
    public void shouldBeAbleToDetectWhenAllTestsHaveFinishedRunning() {
        TestExecutionMonitor.testStarted();
        TestExecutionMonitor.testCompleted();

        TestExecutionMonitor.testStarted();
        TestExecutionMonitor.testCompleted();

        Assertions.assertThat(TestExecutionMonitor.allTestsHaveRun()).isTrue();
    }

    @Test
    public void shouldBeAbleToDetectWhenAllTestsHaveNotFinishedRunning() {
        TestExecutionMonitor.testStarted();
        TestExecutionMonitor.testCompleted();

        TestExecutionMonitor.testStarted();

        Assertions.assertThat(TestExecutionMonitor.allTestsHaveRun()).isFalse();
    }

    @Test
    public void theClassShouldBeAWellDefinedUtilityClass() {
        PatternAssertions.assertClass(TestExecutionMonitor.class).isAWellDefinedUtilityClass();
    }

}
