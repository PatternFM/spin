package fm.pattern.cycle.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fm.pattern.cycle.Instance;
import fm.pattern.cycle.PatternAssertions;
import fm.pattern.cycle.Timeout;

public class CycleConfigurationTest {

    @Before
    public void before() {
        CycleConfiguration.reset();
    }

    @Test
    public void shouldBeAbleToLoadInstancesFromTheDefaultCycleConfigurationFile() {
        CycleConfiguration.load("cycle.yml");

        List<Instance> instances = CycleConfiguration.getInstances();
        assertThat(instances).hasSize(2);

        Instance first = instances.get(0);
        assertThat(first.getName()).isEqualTo("Test Service 1");
        assertThat(first.getPath()).isEqualTo("test-path-1");
        assertThat(first.getPing()).isEqualTo("http://localhost:8080/v1/ping");
        assertThat(first.getStart()).isEqualTo("start.sh");
        assertThat(first.getStop()).isEqualTo("stop.sh");

        Map<String, String> environment = first.getEnvironment();
        assertThat(environment).hasSize(2);
        assertThat(environment).containsKey("key1");
        assertThat(environment).containsKey("key2");

        assertThat(environment.get("key1")).isEqualTo("value1");
        assertThat(environment.get("key2")).isEqualTo("value2");

        Instance second = instances.get(1);
        assertThat(second.getName()).isEqualTo("Test Service 2");
        assertThat(second.getPath()).isEqualTo("test-path-2");
        assertThat(second.getPing()).isEqualTo("http://localhost:9090/v1/ping");
        assertThat(second.getStart()).isEqualTo("start-service.bat");
        assertThat(second.getStop()).isEqualTo("stop-service.bat");
    }

    @Test
    public void shouldBeAbleToProduceDefaultTimeoutSettingsIfTheSettingsAreNotProvided() {
        CycleConfiguration.load("cycle.yml");

        Timeout timeout = CycleConfiguration.getTimeout();
        assertThat(timeout.getPollingInterval()).isEqualTo(CycleConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
        assertThat(timeout.getRetryCount()).isEqualTo(CycleConfiguration.DEFAULT_RETRY_COUNT);
    }

    @Test
    public void shouldBeAbleToOverrideDefaultTimeoutSettingsThroughConfiguration() {
        CycleConfiguration.load("cycle-ci.yml");

        Timeout timeout = CycleConfiguration.getTimeout();
        assertThat(timeout.getPollingInterval()).isEqualTo(5000);
        assertThat(timeout.getRetryCount()).isEqualTo(10);
    }

    @Test
    public void shouldBeAbleToLoadInstancesFromTheCycleConfigSystemProperty() {
        CycleConfiguration.load("cycle-ci.yml");

        List<Instance> instances = CycleConfiguration.getInstances();
        assertThat(instances).hasSize(2);

        Instance first = instances.get(0);
        assertThat(first.getName()).isEqualTo("Test Service 1");
        assertThat(first.getPath()).isEqualTo("test-path-1/extended");
        assertThat(first.getPing()).isEqualTo("http://api.ms1.com/v1/ping");
        assertThat(first.getStart()).isEqualTo("start.sh");
        assertThat(first.getStop()).isEqualTo("stop.sh");

        Instance second = instances.get(1);
        assertThat(second.getName()).isEqualTo("Test Service 2");
        assertThat(second.getPath()).isEqualTo("test-path-2/extended");
        assertThat(second.getPing()).isEqualTo("http://api.ms2.com/v1/ping");
        assertThat(second.getStart()).isEqualTo("start-service.bat");
        assertThat(second.getStop()).isEqualTo("stop-service.bat");
    }

    @Test(expected = CycleConfigurationException.class)
    public void shouldThrowACycleConfigurationExceptionIfTheConfigurationFileCannotBeParsed() {
        CycleConfiguration.load("cycle-invalid.yml");
    }

    @Test(expected = CycleConfigurationException.class)
    public void shouldThrowACycleConfigurationExceptionIfAServiceDoesNotContainAPingAttribute() {
        CycleConfiguration.load("cycle-missing-ping.yml");
        CycleConfiguration.getInstances();
    }

    @Test(expected = CycleConfigurationException.class)
    public void shouldThrowACycleConfigurationExceptionIfAServiceDoesNotContainAPathAttribute() {
        CycleConfiguration.load("cycle-missing-path.yml");
        CycleConfiguration.getInstances();
    }

    @Test
    public void shouldProduceAnEmptyInstanceConfigurationWhenTheConfigurationFileToLoadCannotBeFound() {
        CycleConfiguration.load("cycle-missing.yml");

        Timeout timeout = CycleConfiguration.getTimeout();
        assertThat(timeout.getPollingInterval()).isEqualTo(CycleConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
        assertThat(timeout.getRetryCount()).isEqualTo(CycleConfiguration.DEFAULT_RETRY_COUNT);

        assertThat(CycleConfiguration.getInstances()).hasSize(0);
    }

    @Test
    public void theClassShouldBeAWellDefinedUtilityClass() {
        PatternAssertions.assertClass(CycleConfiguration.class).isAWellDefinedUtilityClass();
    }

}
