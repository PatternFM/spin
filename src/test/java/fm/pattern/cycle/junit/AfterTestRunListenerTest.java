package fm.pattern.cycle.junit;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.Result;

public class AfterTestRunListenerTest {

    @Test
    public void shouldBeAbleToIncrementTheCountOfTestsThatHaveFinsished() throws Exception {
        Assertions.assertThat(TestExecutionMonitor.getTestClassesExecuted()).isEqualTo(0);

        AfterTestRunListener listener = new AfterTestRunListener();
        listener.testRunFinished(new Result());

        Assertions.assertThat(TestExecutionMonitor.getTestClassesExecuted()).isEqualTo(1);
    }

}
