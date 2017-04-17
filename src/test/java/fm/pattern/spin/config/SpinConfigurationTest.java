package fm.pattern.spin.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fm.pattern.spin.Instance;
import fm.pattern.spin.PatternAssertions;
import fm.pattern.spin.StartupConfiguration;

public class SpinConfigurationTest {

    @Before
    public void before() {
        SpinConfiguration.reset();
    }

    @Test
    public void shouldBeAbleToLoadInstancesFromTheDefaultSpinConfigurationFile() {
        SpinConfiguration.load("spin.yml");

        List<Instance> instances = SpinConfiguration.getInstances();
        assertThat(instances).hasSize(2);

        Instance first = instances.get(0);
        assertThat(first.getName()).isEqualTo("Test Service 1");
        assertThat(first.getPing()).isEqualTo("http://localhost:8080/v1/ping");
        assertThat(first.getStart()).isEqualTo("test-path-1/start.sh");
        assertThat(first.getStop()).isEqualTo("test-path-1/stop.sh");

        Map<String, String> environment = first.getEnvironment();
        assertThat(environment).hasSize(2);
        assertThat(environment).containsKey("key1");
        assertThat(environment).containsKey("key2");

        assertThat(environment.get("key1")).isEqualTo("value1");
        assertThat(environment.get("key2")).isEqualTo("value2");

        Instance second = instances.get(1);
        assertThat(second.getName()).isEqualTo("Test Service 2");
        assertThat(second.getPing()).isEqualTo("http://localhost:9090/v1/ping");
        assertThat(second.getStart()).isEqualTo("test-path-2/start-service.bat");
        assertThat(second.getStop()).isEqualTo("test-path-2/stop-service.bat");
    }

    @Test
    public void shouldBeAbleToProduceDefaultTimeoutSettingsIfTheSettingsAreNotProvided() {
        SpinConfiguration.load("spin.yml");

        StartupConfiguration startupConfiguration = SpinConfiguration.getStartupConfiguration();
        assertThat(startupConfiguration.getPollingInterval()).isEqualTo(SpinConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
        assertThat(startupConfiguration.getRetryCount()).isEqualTo(SpinConfiguration.DEFAULT_RETRY_COUNT);
        assertThat(startupConfiguration.isConcurrent()).isEqualTo(SpinConfiguration.DEFAULT_CONCURRENT);
    }

    @Test
    public void shouldBeAbleToOverrideDefaultTimeoutSettingsThroughConfiguration() {
        SpinConfiguration.load("spin-ci.yml");

        StartupConfiguration startupConfiguration = SpinConfiguration.getStartupConfiguration();
        assertThat(startupConfiguration.getPollingInterval()).isEqualTo(5000);
        assertThat(startupConfiguration.getRetryCount()).isEqualTo(10);
        assertThat(startupConfiguration.isConcurrent()).isFalse();
    }

    @Test
    public void shouldBeAbleToLoadInstancesFromTheSpinConfigSystemProperty() {
        SpinConfiguration.load("spin-ci.yml");

        List<Instance> instances = SpinConfiguration.getInstances();
        assertThat(instances).hasSize(2);

        Instance first = instances.get(0);
        assertThat(first.getName()).isEqualTo("Test Service 1");
        assertThat(first.getPing()).isEqualTo("http://api.ms1.com/v1/ping");
        assertThat(first.getStart()).isEqualTo("test-path-1/extended/start.sh");
        assertThat(first.getStop()).isEqualTo("test-path-1/extended/stop.sh");

        Instance second = instances.get(1);
        assertThat(second.getName()).isEqualTo("Test Service 2");
        assertThat(second.getPing()).isEqualTo("http://api.ms2.com/v1/ping");
        assertThat(second.getStart()).isEqualTo("test-path-2/extended/start-service.bat");
        assertThat(second.getStop()).isEqualTo("test-path-2/extended/stop-service.bat");
    }

    @Test(expected = SpinConfigurationException.class)
    public void shouldThrowASpinConfigurationExceptionIfTheConfigurationFileCannotBeParsed() {
        SpinConfiguration.load("spin-invalid.yml");
    }

    @Test(expected = SpinConfigurationException.class)
    public void shouldThrowASpinConfigurationExceptionIfAServiceIsMissingTheStartAttribute() {
        SpinConfiguration.load("spin-no-start.yml");
        SpinConfiguration.getInstances();
    }

    @Test(expected = SpinConfigurationException.class)
    public void shouldThrowASpinConfigurationExceptionIfAServiceIsMissingTheStopAttribute() {
        SpinConfiguration.load("spin-no-stop.yml");
        SpinConfiguration.getInstances();
    }

    @Test(expected = SpinConfigurationException.class)
    public void shouldThrowASpinConfigurationExceptionIfAServiceIsMissingThePingAttribute() {
        SpinConfiguration.load("spin-no-ping.yml");
        SpinConfiguration.getInstances();
    }

    @Test
    public void shouldProduceAnEmptyInstanceConfigurationWhenTheConfigurationFileToLoadCannotBeFound() {
        SpinConfiguration.load("spin-missing.yml");

        StartupConfiguration timeout = SpinConfiguration.getStartupConfiguration();
        assertThat(timeout.getPollingInterval()).isEqualTo(SpinConfiguration.DEFAULT_POLLING_INTERVAL_MILLIS);
        assertThat(timeout.getRetryCount()).isEqualTo(SpinConfiguration.DEFAULT_RETRY_COUNT);

        assertThat(SpinConfiguration.getInstances()).hasSize(0);
    }

    @Test
    public void theClassShouldBeAWellDefinedUtilityClass() {
        PatternAssertions.assertClass(SpinConfiguration.class).isAWellDefinedUtilityClass();
    }

}
