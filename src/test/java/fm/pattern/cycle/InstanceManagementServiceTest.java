package fm.pattern.cycle;

import org.junit.Before;
import org.junit.Test;

public class InstanceManagementServiceTest {

    private Instance instance;

    @Before
    public void before() {
        this.instance = new Instance("name", ".", "http://localhost:8080/v1/ping");
    }

    @Test(expected = InstanceManagementException.class)
    public void shouldNotBeAbleToExecuteAStartScriptWhenTheStartScriptIsNotPresent() {
        this.instance.setStart("not_a_real_start_script.sh");
        InstanceManagementService.start(instance);
    }

    @Test
    public void shouldBeAbleToExecuteAStartScriptWhenTheStartScriptIsPresentAndReturnsAnExitCodeOfZero() {
        instance.setStart("test-start.sh");
        InstanceManagementService.start(instance);
    }

    @Test(expected = InstanceManagementException.class)
    public void shouldNotBeAbleToExecuteAStopScriptWhenTheStartScriptIsNotPresent() {
        this.instance.setStart("not_a_real_stop_script.sh");
        InstanceManagementService.stop(instance);
    }

    @Test
    public void shouldBeAbleToExecuteAStopScriptWhenTheStopScriptIsPresentAndReturnsAnExitCodeOfZero() {
        instance.setStop("test-stop.sh");
        InstanceManagementService.stop(instance);
    }

    @Test
    public void theClassShouldBeAWellDefinedUtilityClass() {
        PatternAssertions.assertClass(InstanceManagementService.class).isAWellDefinedUtilityClass();
    }

}
